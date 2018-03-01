package operation;

import java.util.Comparator;

public class sort implements Comparator{

	@Override
	public int compare(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
       float[] f1=(float[])arg0;
       float[] f2=(float[])arg1;
       if(f1[0]>f2[0])
       {   return 1;
       }else {
    	   return -1;
       }
		
	}

}
