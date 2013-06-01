package org.eclipse.jst.pagedesigner.itemcreation;

import java.util.Comparator;

public class ComparatorUtil implements Comparator{

//	public int compare(String string1, String string2) {
//		return string1.compareTo(string2);
//	}
	
	
	
	public static void main(String[] args){
		
//		ArrayList <String>list1 = new ArrayList<String>();
//		
//		list1.add("zzz5");
//		list1.add("zzz8");
//		list1.add("zzz7");
//		list1.add("zzz1");
//		list1.add("zzz2");
//		
//		ComparatorUtil comparator = new ComparatorUtil();
//		Collections.sort(list1, comparator);
		
		System.out.println("1".compareTo("2")); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("2".compareTo("2")); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("2".compareTo("1")); //$NON-NLS-1$ //$NON-NLS-2$
	}



	public int compare(Object o1, Object o2) {
		// TODO Auto-generated method stub
		return o1.toString().compareTo(o2.toString());
	}
	
}
