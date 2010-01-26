/*
 * Copyright (C) 2009 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.odkclinic.client.graph;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;
import android.webkit.WebView;

/**
 * Handler for interaction between flot javascript and java in andriod.
 * 
 * @author Jessica Leung
 * 
 */
public class FlotGraphHandler {
	
	public enum MODE{LINE_GRAPH, BOOLEAN_GRAPH, TIMELINE}
	
	static String LOG_TAG = FlotGraphHandler.class.getName();
	private WebView mAppView;
	private OnClickListener onClickListener;
	private OnGraphRenderedListener onGraphRenderedListener;
	private JSONArray data;
	private MODE mode;
	
	/** Padding in pixels around graph within WebView **/
	private static int graphPadding = 10;
	private static int bottomPadding = 30;

	public FlotGraphHandler(WebView appView, MODE mode) {
		this.mAppView = appView;
		this.mode = mode;
	}
	
	public FlotGraphHandler(WebView appView, MODE mode, JSONArray data) {
		this.mAppView = appView;
		this.mode = mode;
		this.data = data;
	}

	public String getGraphTitle() {
		return "This is my graph, baby!";
	}

	public int getGraphWidth() {
		return mAppView.getWidth() - graphPadding * 2;
	}

	public int getGraphHeight() {
		return +mAppView.getHeight() - graphPadding * 2 - bottomPadding;
	}

	public void loadGraph() {
		JSONArray arr = new JSONArray();
		JSONObject options = new JSONObject();
		Boolean display = false;
		try {
			//display of click values
			if(mode == MODE.LINE_GRAPH){
				display= true;
			}

			
			//data arguments
			JSONObject result = new JSONObject();
			result.put("data", data);
			if(mode == MODE.LINE_GRAPH){
				result.put("lines", new JSONObject().put("show", true)); // { "lines": { "show" : true }},
			}
			result.put("points", new JSONObject().put("show", true)); // { "points": { "show" : true }}
			arr.put(result);
			
			//series option arguments
			JSONObject grid = new JSONObject();
			grid.put("clickable",true); //{"grid" : {"clickable":true}}
			grid.put("mouseActiveRadius",40);
			//grid.put("color","#999999");
			options.put("grid",grid);
			
			JSONObject xaxis = new JSONObject();
			xaxis.put("mode", "time");
			options.put("xaxis", xaxis);
			
			//adjust y axis for non-numerical graphs
			if(mode == MODE.BOOLEAN_GRAPH){
				JSONObject yaxis = new JSONObject();
				yaxis.put("ticks", new JSONArray("[[0,\"false\"],[1,\"true\"]]"));
				yaxis.put("min", -1);
				yaxis.put("max", 2);
				options.put("yaxis", yaxis);				
			}else if(mode == MODE.TIMELINE){
				JSONObject yaxis = new JSONObject();
				yaxis.put("ticks", new JSONArray("[[1,\"recorded\"]]"));
				options.put("yaxis", yaxis);				
			}
		
			
		} catch (Exception ex) {
			Log.e(LOG_TAG, ex.getMessage());
		}
		
		// return arr.toString(); //This _WILL_ return the data in a good
		// looking JSON string, but if you pass it straight into the Flot Plot
		// method, it will not work!

		mAppView.loadUrl("javascript:GotGraph(" + arr.toString()+","+ options+ ","+display.toString().toLowerCase()+")"); 
		
	}
	
	public void reloadGraph(){
		mAppView.loadUrl("javascript:load()"); 
	}
	
	public void onClick(int position){
		if(onClickListener != null){
			onClickListener.onClick(position);
		}
		
	} 
	
	public void onGraphRendered(){
		if(onGraphRenderedListener != null){
			onGraphRenderedListener.onGraphRendered();
		}
	}
	
	public OnClickListener getOnClickListener() {
		return onClickListener;
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public JSONArray getData() {
		return data;
	}

	public void setData(JSONArray data) {
		this.data = data;
		reloadGraph();
	}

	public OnGraphRenderedListener getOnGraphRenderedListener() {
		return onGraphRenderedListener;
	}

	public void setOnGraphRenderedListener(
			OnGraphRenderedListener onGraphRenderedListener) {
		this.onGraphRenderedListener = onGraphRenderedListener;
	}
	
}
