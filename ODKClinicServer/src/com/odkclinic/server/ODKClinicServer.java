
package com.odkclinic.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.EnumMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.fileupload.MultipartStream.MalformedStreamException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;

import com.odkclinic.download.AndroidDownloadManager;
import com.odkclinic.model.bundle.Bundle;
import com.odkclinic.model.bundle.EncounterBundle;
import com.odkclinic.model.bundle.ObservationBundle;
import com.odkclinic.server.ODKClinicConstants.Headers;

public class ODKClinicServer
{
    public ODKClinicServer() {
        
    }
    
    private AndroidDownloadManager dl;
    
    public boolean start() {
        if (dl == null) {
            dl = AndroidDownloadManager.getInstance();
        }
        return dl == null;
    }
    
    public void shutdown() {
        if (dl != null) {
            AndroidDownloadManager.returnInstance(dl);
        }
    }

    private Log log = LogFactory.getLog(this.getClass());

    private enum UploadType
    {
        Observations, Encounters
    };

    private String getString(MultipartStream multipartStream)
            throws MalformedStreamException, IOException
    {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        multipartStream.readBodyData(data);
        return new String(data.toByteArray());
    }

    private boolean isNull(Object... d)
    {
        for (Object o : d)
        {
            if (o == null)
                return true;
        }
        return false;
    }

    /**
     * Called to handle data input and output streams, reading requests and
     * writing response
     * 
     * @param request
     * @param response
     */
    @SuppressWarnings("deprecation")
    public void handleStreams(HttpServletRequest request,
            HttpServletResponse response) throws IOException, Exception
    {
        if (dl == null)
            throw new IllegalStateException("Server not initialized yet.");
        byte responseStatus = ODKClinicConstants.STATUS_SUCCESS;
        int boundaryIndex = request.getContentType().indexOf("boundary=") + 9;
        byte[] boundary = (request.getContentType().substring(boundaryIndex))
                .getBytes();
        MultipartStream multipartStream = new MultipartStream(request
                .getInputStream(), boundary);
        boolean nextPart = multipartStream.skipPreamble();

        // for now assume the user, pass, seriazier, and revtoken are passed in
        // first and in order
        String user = null, skey = null, pass = null;
        Long revToken = null;
        Headers current = Headers.USER;
        while (nextPart)
        {
            String headers = parseHeader(multipartStream.readHeaders());
            boolean getOut = false;
            current = Headers.valueOf(headers);
            switch (current)
            {
                case USER:
                    user = getString(multipartStream);
                    break;
                case PASS:
                    pass = getString(multipartStream);
                    break;
                case SKEY:
                    skey = getString(multipartStream);
                    break;
                case REVTOKEN:
                    revToken = Long.parseLong(getString(multipartStream));
                    break;
                default:
                    getOut = true;
                    break;
            }

            if (getOut)
                break;
            nextPart = multipartStream.readBoundary();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (isNull(user, skey, pass, revToken))
        {
            responseStatus = ODKClinicConstants.STATUS_ERROR;
            log.error("Failed gettings parts from multipart stream: Element is null");
        } else
        {
            try
            {
                Context.openSession();
                try
                {
                    Context.authenticate(user, pass);
                } catch (ContextAuthenticationException e)
                {
                    responseStatus = ODKClinicConstants.STATUS_ACCESS_DENIED;
                    log.error(String.format("Failed to authenticate user: %s with pass %s", user, pass), e);
                } catch (Exception e)
                {
                    responseStatus = ODKClinicConstants.STATUS_ERROR;
                    log.error("Serious error occured with authentication.", e);
                }

                if (responseStatus == ODKClinicConstants.STATUS_SUCCESS)
                {
                    Map<UploadType, Bundle<?>> map = new EnumMap<UploadType, Bundle<?>>(UploadType.class);

                    while ((current == Headers.UPLOAD_ENCOUNTER
                            || current == Headers.UPLOAD_OBSERVATION) &&
                            responseStatus == ODKClinicConstants.STATUS_SUCCESS)
                    {
                        Bundle<?> bundle = null;
                        ByteArrayOutputStream data = new ByteArrayOutputStream();
                        multipartStream.readBodyData(data);
                        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data.toByteArray()));
                        System.out.println(current);
                        switch (current)
                        {
                            case UPLOAD_ENCOUNTER:
                                bundle = uploadEncounters(dis);
                                if (bundle != null)
                                {
                                    map.put(UploadType.Encounters, bundle);
                                } else 
                                {
                                    responseStatus = ODKClinicConstants.STATUS_ERROR;
                                }
                                break;
                            case UPLOAD_OBSERVATION:
                                bundle = uploadObservations(dis);
                                if (bundle != null)
                                {
                                    map.put(UploadType.Observations, bundle);
                                } else 
                                {
                                    responseStatus = ODKClinicConstants.STATUS_ERROR;
                                }
                                break;
                        }

                        multipartStream.readBoundary();
                        String headers = parseHeader(multipartStream.readHeaders());
                        current = Headers.valueOf(headers);

                    }

                    if (responseStatus == ODKClinicConstants.STATUS_SUCCESS)
                    {
                        // try to commit changes to server database first
                        if (commitChanges(map, user, revToken)
                                && current == Headers.DOWNLOAD_ACTIONS)
                        {
                            String[] actions = getString(multipartStream)
                                    .split(";");
                            DataOutputStream dos = new DataOutputStream(baos);
                            boolean success = false;
                            for (String action : actions)
                            {
                                switch (Headers.valueOf(action))
                                {
                                    case DOWNLOAD_ENCOUNTER:
                                        success = downloadEncounters(dos, skey, revToken);
                                        break;
                                    case DOWNLOAD_PATIENT:
                                        success = downloadPatients(dos, skey);
                                        break;
                                    case DOWNLOAD_PROGRAM:
                                        success = downloadPrograms(dos, skey);
                                        break;
                                    case DOWNLOAD_OBSERVATION:
                                        success = downloadObservations(dos, skey, revToken);
                                        break;
                                    case DOWNLOAD_LOCATION:
                                        success = downloadLocations(dos, skey);
                                        break;
                                    case DOWNLOAD_PATIENTPROGRAM:
                                        success = downloadPatientPrograms(dos, skey);
                                        break;
                                    case DOWNLOAD_CONCEPTNAME:
                                        success = downloadConceptNames(dos, skey);
                                        break;
                                    case DOWNLOAD_COHORT:
                                        success = downloadCohorts(dos, skey);
                                        break;
                                    case DOWNLOAD_COHORTMEMBER:
                                        success = downloadCohortMembers(dos, skey);
                                        break;
                                    case DOWNLOAD_CONCEPT:
                                        success = downloadConcepts(dos, skey);
                                        break;
                                    case DOWNLOAD_PROGRAMWORKFLOW:
                                        success = downloadProgramWorkflows(dos, skey);
                                        break;
                                }
                                if (!success) {
                                    responseStatus = ODKClinicConstants.STATUS_ERROR;
                                    log.error("Failedfailed sending bundles to client.");
                                    break;
                                }
                            }
                            dos.write(ODKClinicConstants.ACTION_ANDROID_END);
                            dos.writeLong(dl.getUserRevisionToken());
                        } else
                        {
                            responseStatus = ODKClinicConstants.STATUS_ERROR;
                            log.error("Failed committing bundles from client to database.");
                        }
                    } else {
                        responseStatus = ODKClinicConstants.STATUS_ERROR;
                        log.error("Failed getting bundles from client.");
                    }
                }
            } finally
            {
                Context.closeSession();

            }
        }
        switch (responseStatus) {
            case ODKClinicConstants.STATUS_SUCCESS:
                response.setStatus(HttpServletResponse.SC_OK);
                byte[] bytes = baos.toByteArray();
                OutputStream os = response.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
                break;
            case ODKClinicConstants.STATUS_ACCESS_DENIED:
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                break;
            case ODKClinicConstants.STATUS_ERROR:
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                break;
        }
    }

    private boolean commitChanges(Map<UploadType, Bundle<?>> map, String user, long revToken)
    {
        boolean success = false;
        if (map.size() == 0)
            success = true;
        for (Map.Entry<UploadType, Bundle<?>> entry : map.entrySet())
        {
            log.debug("Commiting to the database: " + entry.getKey().toString());
            
            switch (entry.getKey())
            {
                case Encounters:
                    if (dl
                            .commitEncounters((EncounterBundle) entry
                                    .getValue(), user, revToken))
                        success = true;
                    break;
                case Observations:
                    if (dl
                            .commitObservations((ObservationBundle) entry
                                    .getValue(), user, revToken))
                        success = true;
                    break;
            }
           
        }
        if (success && map.size() > 0) { // update user revtoken table
            log.info("Updating revision for user: " + user);
            ODKClinicService revService = (ODKClinicService)Context.getService(ODKClinicService.class);
            revService.updateUserRevisionToken(user);
        } else if (!success && map.size() > 0) {
            log.error("failed to commit to database");
        } else {
            log.info("No updates from client.");
        }
        return success;
    }
    private String parseHeader(String value) {
        String headers = value.split(";")[1];
        headers = headers.split("name=")[1];
        headers = headers.trim();
        headers = headers.substring(1, headers.length() - 1);
        return headers;
    }
    private boolean downloadEncounters(DataOutputStream os, String serializerKey,
            long revToken) throws Exception
    {
       return dl.downloadBundle(os, ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_ENCOUNTER, revToken);
    }

    private Bundle<?> uploadEncounters(DataInputStream is) throws IOException
    {
        return dl.uploadBundle(is, ODKClinicConstants.ACTION_ANDROID_UPLOAD_ENCOUNTER);
    }

    private boolean downloadObservations(DataOutputStream os, String serializerKey,
            long revToken) throws Exception
    {
        return dl.downloadBundle(os, ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_OBS, revToken);
    }

    private Bundle<?> uploadObservations(DataInputStream is) throws IOException
    {
        return dl.uploadBundle(is, ODKClinicConstants.ACTION_ANDROID_UPLOAD_OBS);
    }

    private boolean downloadPatients(DataOutputStream dos, String serializerKey)
            throws Exception
    {
       return dl.downloadBundle(dos, ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_PATIENTS, 0);
    }

    private boolean downloadPrograms(DataOutputStream dos, String serializerKey)
            throws Exception
    {
        return dl.downloadBundle(dos, ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_PROGRAMS, 0);
    }
    
    private boolean downloadConcepts(DataOutputStream dos,String serializerKey) {
        return dl.downloadBundle(dos, ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_CONCEPTS, 0);
    }
    
    private boolean downloadConceptNames(DataOutputStream dos,String serializerKey) {
        return dl.downloadBundle(dos, ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_CONCEPTNAMES, 0);
    }
    
    private boolean downloadCohorts(DataOutputStream dos,String serializerKey) {
        return dl.downloadBundle(dos,ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_COHORTS, 0);
    }
    
    private boolean downloadCohortMembers(DataOutputStream dos,String serializerKey) {
        return dl.downloadBundle(dos, ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_COHORTMEMBERS, 0);
    }
    
    private boolean downloadLocations(DataOutputStream dos,String serializerKey) {
        return dl.downloadBundle(dos, ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_LOCATIONS, 0);
    }
    
    private boolean downloadPatientPrograms(DataOutputStream dos,String serializerKey) {
        return dl.downloadBundle(dos, ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_PATIENTPROGRAMS, 0);
    }
    
    private boolean downloadProgramWorkflows(DataOutputStream dos, String serializerKey) {
        return dl.downloadBundle(dos, ODKClinicConstants.ACTION_ANDROID_DOWNLOAD_PROGRAMWORKFLOWS, 0);
    }
}
