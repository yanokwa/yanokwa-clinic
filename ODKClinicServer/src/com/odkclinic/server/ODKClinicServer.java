
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
import com.odkclinic.model.Bundle;
import com.odkclinic.model.EncounterBundle;
import com.odkclinic.model.ObservationBundle;
import com.odkclinic.server.ODKClinicConstants.Headers;

public class ODKClinicServer
{

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
        byte responseStatus = ODKClinicConstants.STATUS_SUCCESS;
        int boundaryIndex = request.getContentType().indexOf("boundary=") + 9;
        System.out.println(request.getContentType());
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
            String headers = multipartStream.readHeaders();
            headers = headers.split(";")[1];
            headers = headers.split("name=")[1];
            headers = headers.trim();
            headers = headers.substring(1, headers.length() - 1);
            boolean getOut = false;
            current = Headers.valueOf(headers);
            System.out.println(headers);
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
            System.out
                    .println("Failed gettings parts from multipart stream: Element is null");
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
                    e.printStackTrace();
                } catch (Exception e)
                {
                    responseStatus = ODKClinicConstants.STATUS_ERROR;
                }

                if (responseStatus == ODKClinicConstants.STATUS_SUCCESS)
                {
                    Map<UploadType, Bundle<?>> map = new EnumMap<UploadType, Bundle<?>>(
                            UploadType.class);

                    while (current == Headers.UPLOAD_ENCOUNTER
                            || current == Headers.UPLOAD_OBSERVATION)
                    {
                        Bundle<?> bundle = null;
                        ByteArrayOutputStream data = new ByteArrayOutputStream();
                        multipartStream.readBodyData(data);

                        switch (current)
                        {
                            case UPLOAD_ENCOUNTER:
                                bundle = uploadEncounters(new DataInputStream(
                                        new ByteArrayInputStream(data
                                                .toByteArray())), skey);
                                if (bundle != null)
                                {
                                    map.put(UploadType.Encounters, bundle);
                                }
                                break;
                            case UPLOAD_OBSERVATION:
                                bundle = uploadObservations(new DataInputStream(
                                        new ByteArrayInputStream(data
                                                .toByteArray())), skey);
                                if (bundle != null)
                                {
                                    map.put(UploadType.Observations, bundle);
                                }
                                break;
                        }

                        multipartStream.readBoundary();
                        String headers = multipartStream.readHeaders();
                        headers = headers.split(";")[1];
                        headers = headers.split("name=")[1];
                        headers = headers.trim();
                        headers = headers.substring(1, headers.length() - 1);
                        current = Headers.valueOf(headers);

                    }

                    if (responseStatus == ODKClinicConstants.STATUS_SUCCESS)
                    {
                        // try to commit changes to server database first
                        if (commitChanges(map, revToken)
                                && current == Headers.DOWNLOAD_ACTIONS)
                        {
                            String[] actions = getString(multipartStream)
                                    .split(";");
                            DataOutputStream dos = new DataOutputStream(baos);

                            for (String action : actions)
                            {
                                switch (Headers.valueOf(action))
                                {
                                    case DOWNLOAD_ENCOUNTER:
                                        downloadEncounters(dos, skey, revToken);
                                        break;
                                    case DOWNLOAD_PATIENT:
                                        downloadPatients(dos, skey);
                                        break;
                                    case DOWNLOAD_PROGRAM:
                                        downloadPrograms(dos, skey);
                                        break;
                                    case DOWNLOAD_OBSERVATION:
                                        downloadObservations(dos, skey, revToken);
                                        break;
                                    case DOWNLOAD_LOCATION:
                                        downloadLocations(dos, skey);
                                        break;
                                    case DOWNLOAD_PATIENTPROGRAM:
                                        downloadPatientPrograms(dos, skey);
                                        break;
                                    case DOWNLOAD_CONCEPTNAME:
                                        downloadConceptNames(dos, skey);
                                        break;
                                    case DOWNLOAD_COHORT:
                                        downloadCohorts(dos, skey);
                                        break;
                                    case DOWNLOAD_COHORTMEMBER:
                                        downloadCohortMembers(dos, skey);
                                        break;
                                    case DOWNLOAD_CONCEPT:
                                        downloadConcepts(dos, skey);
                                        break;
                                }
                            }
                        } else
                        {
                            responseStatus = ODKClinicConstants.STATUS_ERROR;
                        }
                    }
                }
            } finally
            {
                Context.closeSession();

            }
        }

        if (responseStatus == ODKClinicConstants.STATUS_SUCCESS)
        {
            response.setStatus(200);
            byte[] bytes = baos.toByteArray();
            OutputStream os = response.getOutputStream();
            System.out.println(bytes.length);
            os.write(bytes);
            os.flush();
            os.close();
        } else
        {
            response.setStatus(500);
        }
    }

    private boolean commitChanges(Map<UploadType, Bundle<?>> map, long revToken)
    {
        for (Map.Entry<UploadType, Bundle<?>> entry : map.entrySet())
        {
            switch (entry.getKey())
            {
                case Encounters:
                    if (!AndroidDownloadManager
                            .commitEncounters((EncounterBundle) entry
                                    .getValue(), revToken))
                        return false;
                    break;
                case Observations:
                    if (!AndroidDownloadManager
                            .commitObservations((ObservationBundle) entry
                                    .getValue(), revToken))
                        return false;
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    private void downloadEncounters(OutputStream os, String serializerKey,
            long revToken) throws Exception
    {

        AndroidDownloadManager.downloadEncounters(os, serializerKey, revToken);

    }

    private EncounterBundle uploadEncounters(DataInputStream is,
            String serializerKey) throws IOException
    {
        return AndroidDownloadManager.uploadEncounters(is, serializerKey);
    }

    private void downloadObservations(OutputStream os, String serializerKey,
            long revToken) throws Exception
    {

        AndroidDownloadManager
                .downloadObservations(os, serializerKey, revToken);

    }

    private ObservationBundle uploadObservations(DataInputStream is,
            String serializerKey) throws IOException
    {

        return AndroidDownloadManager.uploadObservations(is, serializerKey);

    }

    private void downloadPatients(DataOutputStream dos, String serializerKey)
            throws Exception
    {

        AndroidDownloadManager.downloadPatients(dos, serializerKey);

    }

    private void downloadPrograms(DataOutputStream dos, String serializerKey)
            throws Exception
    {

        AndroidDownloadManager.downloadPrograms(dos, serializerKey);

    }
    
    private void downloadConcepts(DataOutputStream dos,String serializerKey) {
        AndroidDownloadManager.downloadConcepts(dos, serializerKey);
    }
    
    private void downloadConceptNames(DataOutputStream dos,String serializerKey) {
        AndroidDownloadManager.downloadConceptNames(dos, serializerKey);
    }
    
    private void downloadCohorts(DataOutputStream dos,String serializerKey) {
        AndroidDownloadManager.downloadCohorts(dos, serializerKey);
    }
    
    private void downloadCohortMembers(DataOutputStream dos,String serializerKey) {
        AndroidDownloadManager.downloadCohortMembers(dos, serializerKey);
    }
    
    private void downloadLocations(DataOutputStream dos,String serializerKey) {
        AndroidDownloadManager.downloadLocations(dos, serializerKey);
    }
    
    private void downloadPatientPrograms(DataOutputStream dos,String serializerKey) {
        AndroidDownloadManager.downloadPatientPrograms(dos, serializerKey);
    }

}
