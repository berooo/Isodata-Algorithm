package entity;

import java.util.ArrayList;
import java.util.List;

public class category {
	private String name;
	private List<point> ls;
	private point center;
	
	public category() {
		// TODO Auto-generated constructor stub
		ls=new ArrayList<point>();
	}

	public void setName(String name) {
		this.name=name;
	}
	
	public String getName() {
		return name;
	}
	
	public void add(point p) {
		ls.add(p);
	}
	public List<point> getList(){
		return ls;
	}

	public point getCenter() {
		return center;
	}
    //类内平均距离
    public float calMeanDistance() {
    	
    	float sum=0;
    	for(int i=0;i<ls.size();i++) {
    		sum+=Euclidean(ls.get(i),center);
    	}
    	float distance=sum/ls.size();
		return distance;
    	
    }
    public float Euclidean(point a,point b) {
		
		float[] aValue=a.getValue();
		float[] bValue=b.getValue();
		
		float sum=0;
		for(int i=0;i<aValue.length;i++) {
			sum+=Math.pow((aValue[i]-bValue[i]), 2);
		}
		
		return (float) Math.sqrt(sum);
		
	}
    public float[] calStandardVectorDifference() {
    	
    	
    	int dimension=ls.get(0).getDimension();
    	float[] vector=new float[dimension];
    	
    	for(int i=0;i<dimension;i++) {
    		//float[] a=ls.get(i).getValue();
    		float sum=0;
    		for(int j=0;j<ls.size();j++) {
    			float[] a=ls.get(j).getValue();
    			float[] b=center.getValue();
    			sum+=Math.pow(a[i]-b[i], 2);
    		}
    		vector[i]=(float)Math.sqrt(sum/ls.size());
    		
    	}
    		
		return vector;
    	
    }
   public void updateCenter() {
		
		if(ls.size()==1) {
			center=ls.get(0);
		}else {
			int d=ls.get(0).getDimension();
			float[] a = new float[d];
			for(int i=0;i<d;i++) {
				float sum=0;
				for(int j=0;j<ls.size();j++) {
					float[] b=ls.get(j).getValue();
					sum+=b[i];
				}
				a[i]=sum/ls.size();
			}
			center.setValue(a);
		}
	}
    public void setCenter(point center)
    {
    	this.center=center;
    }
	public boolean checkExist(point a) {

		if(ls.contains(a)) {
			return true;
		}
		return false;
	}
}
