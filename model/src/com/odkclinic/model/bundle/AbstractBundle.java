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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.xforms.serialization.Persistent;
import org.openmrs.module.xforms.serialization.SerializationUtils;

/**
 * @author Euzel Villanueva
 * 
 */
abstract class AbstractBundle<E extends Persistent> implements Bundle<E> {
	private List<E> bundle;
	private final Class<E> cls;

	public AbstractBundle(Class<E> cls) {
		bundle = new ArrayList<E>();
		this.cls = cls;
	}

	public final void add(E element) {
		bundle.add(element);
	}

	public final List<E> getBundle() {
		return bundle;
	}

	private final void setBundle(List<E> bundle) {
		this.bundle = bundle;
	}

	@SuppressWarnings("unchecked")
	public final void read(DataInputStream dis) throws IOException,
			InstantiationException, IllegalAccessException {
		List bundle = SerializationUtils.read(dis, cls);
		if (bundle != null) {
			setBundle(bundle);
		}
	}

	public final void write(DataOutputStream dos) throws IOException {
		SerializationUtils.write(bundle, dos);
	}

	public final byte[] getBytes() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		write(dos);
		return baos.toByteArray();
	}
}
