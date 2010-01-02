//package com.odkclinic.client.xforms;
//
//import java.util.List;
//
//public class Bundle<E extends AbstractRecord> {
//	private Vector<E> bundle;
//	
//	public Bundle() {
//		bundle = new List<E>();
//	}
//	
//	public void add(E element) {
//		bundle.add(element);
//	}
//	
//	public void read(DataInputStream dis) {
//		bundle = PersistentHelper.read(dis, E.class);
//	}
//	
//	public void write(DataOutputStream dos) {
//		PersistentHelper.write(bundle, dos);
//	}
//}
