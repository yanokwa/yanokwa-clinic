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

package com.odkclinic.client.db;

import com.odkclinic.client.db.tables.OpenMRS;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Protected class for database opening, creation, and upgrading
 * 
 * @author Euzel Villanueva
 *
 */
class DatabaseHelper extends SQLiteOpenHelper {
	DatabaseHelper(Context context) {
        super(context, OpenMRS.DATABASE_NAME, null, OpenMRS.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	for (String createString: OpenMRS.createStatements())
    		db.execSQL(createString);
    	for (String trigger: DbConstants.TRIGGERS) 
    		db.execSQL(trigger);
        for (String query: DbConstants.DEFAULT_VALUES)
        	db.execSQL(query);
        for (String index: DbConstants.INDEXES) {
        	db.execSQL(index);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(OpenMRS.TAG, 
        		String.format("Upgrading database from version %d to %d, which will destroy all old data",
                oldVersion, newVersion));
        for (String dropString: OpenMRS.dropStatements())
        	db.execSQL(dropString);
        onCreate(db);
    }
}