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
package com.odkclinic.model.bundle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import org.openmrs.module.xforms.serialization.Persistent;

/**
 * @author Euzel Villanueva
 *
 */
public interface Bundle<E extends Persistent> 
{
    public void add(E element);
    public List<E> getBundle();
    public void read(DataInputStream dis)throws IOException, InstantiationException, IllegalAccessException;
    public void write(DataOutputStream dos) throws IOException;
    public byte[] getBytes() throws IOException;
}
