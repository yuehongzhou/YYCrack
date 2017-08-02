package ncnipc.androidgroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MapUtils {
	//private static double EARTH_RADIUS = 6378.137;
	private static double EARTH_RADIUS = 6371.393;
	private static double rad(double d)
	{
	   return d * Math.PI / 180.0;
	}
	
	private static double calAValue = 111.32;
	//111.13355555555556
	private static double calBValue = 0.0;
	
	
	
	/*public static void main(String[] args) {
		System.out.println(MapUtils.GetDistance(38.9042099090,121.5676035251,38.904209909,121.55602675348212));
		
		MapUtils.ConvertDistanceToLogLat(1.00, 38.9042099090,121.5676035251, -90.0);
	}*/
	
	

	public static double getCalAValue() {
		return calAValue;
	}

	public static double getEARTH_RADIUS() {
		return EARTH_RADIUS;
	}

	public static void setEARTH_RADIUS(double eARTH_RADIUS) {
		EARTH_RADIUS = eARTH_RADIUS;
	}

	public static void setCalAValue(double calAValue) {
		MapUtils.calAValue = calAValue;
	}

	public static double getCalBValue() {
		return calBValue;
	}

	public static void setCalBValue(double calBValue) {
		MapUtils.calBValue = calBValue;
	}

	/**
	 * 计算两个经纬度之间的距离
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return
	 */
	public static double GetDistance(double lat1, double lng1, double lat2, double lng2)
	{
	   double radLat1 = rad(lat1);
	   double radLat2 = rad(lat2);
	   double a = radLat1 - radLat2;	
	   double b = rad(lng1) - rad(lng2);
	   double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + 
	    Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
	   s = s * EARTH_RADIUS;
//	   s = Math.round(s * 1000);
	   return s;
	}
	
	public static Coordinate ConvertDistanceToLogLat(double distance, double lat1, double lng1, double angle)
    {
		Coordinate coordinate = new Coordinate();
       
        double lon = lng1 + (distance * Math.sin(angle* Math.PI / 180)) / (111 * Math.cos(lat1 * Math.PI / 180));//将距离转换成经度的计算公式
        double lat = lat1 + (distance * Math.cos(angle* Math.PI / 180)) / 111;//将距离转换成纬度的计算公式
        
        coordinate.setLatitude(lat);
        coordinate.setLongitude(lon);
        
		return coordinate;
       
    }
	
	
	public static double calAngleBaseLat(double angle)
	{
		double re_angle = 0;
		double angle1 = Math.abs(angle-90);
		double angle2 = Math.abs(angle-270);
		
		if(angle1<angle2)
			re_angle=angle1;
		else
			re_angle=angle2;
		
		return re_angle;
		
	}
	
	
	public static Coordinate calYYCoordinate(double m1, double n1, double l1, double m2, double n2, double l2, double m3, double n3, double l3)
	{
		Coordinate coordinate = new Coordinate();
		coordinate.setLatitude(0);
		coordinate.setLongitude(0);
		
		List<Coordinate> tmpCooList = new ArrayList<Coordinate>();
		
//		double azimuth = CalculationLogLatDistance.getAzimuth3(m1, n1, m2, n2);
		
		double azimuth = CalculationLogLatDistance.getAzimuth2(m1, n1, m2, n2);
		azimuth = calAngleBaseLat(azimuth);
		
		double s1=azimuth*Math.PI/180;
		
		
		double l12=GetDistance(m1,n1,m2,n2);
		double cos_s2=(l1*l1+l12*l12-l2*l2)/(2*l1*l12);
		double s2=Math.acos(cos_s2);
		
		double dai_lon1=(180*l1*Math.cos(s1+s2))/(Math.PI*EARTH_RADIUS*Math.cos((m1*Math.PI)/180));
		double dai_lat1=(180*l1*Math.sin(s1+s2))/(Math.PI*EARTH_RADIUS);

		double dai_lon2=(180*l1*Math.cos(s1-s2))/(Math.PI*EARTH_RADIUS*Math.cos((m1*Math.PI)/180));
		double dai_lat2=(180*l1*Math.sin(s1-s2))/(Math.PI*EARTH_RADIUS); 
		
		Coordinate coo = new Coordinate();
		
		coo.setLatitude(m1+dai_lat1);
		coo.setLongitude(n1+dai_lon1);
		tmpCooList.add(coo);
		
        coo = new Coordinate();
		coo.setLatitude(m1+dai_lat1);
		coo.setLongitude(n1-dai_lon1);
		tmpCooList.add(coo);
		
		coo = new Coordinate();
		coo.setLatitude(m1-dai_lat1);
		coo.setLongitude(n1+dai_lon1);
		tmpCooList.add(coo);
		
		coo = new Coordinate();
		coo.setLatitude(m1-dai_lat1);
		coo.setLongitude(n1-dai_lon1);
		tmpCooList.add(coo);
		
		
        coo = new Coordinate();		
		coo.setLatitude(m1+dai_lat2);
		coo.setLongitude(n1+dai_lon2);
		tmpCooList.add(coo);
		
        coo = new Coordinate();
		coo.setLatitude(m1+dai_lat2);
		coo.setLongitude(n1-dai_lon2);
		tmpCooList.add(coo);
		
		coo = new Coordinate();
		coo.setLatitude(m1-dai_lat2);
		coo.setLongitude(n1+dai_lon2);
		tmpCooList.add(coo);
		
		coo = new Coordinate();
		coo.setLatitude(m1-dai_lat2);
		coo.setLongitude(n1-dai_lon2);
		tmpCooList.add(coo);
		
		
		Coordinate bestCoordinate = new Coordinate();
		double min_distance_differ = Double.MAX_VALUE;
		
		for(Coordinate coo1 : tmpCooList)
		{
			double distance = CalculationLogLatDistance.GetDistanceTwo(coo1.getLongitude(), coo1.getLatitude(), n3, m3);
			double distance_differ = Math.abs(distance-l3);
			
			if(distance_differ<min_distance_differ)
			{
				min_distance_differ = distance_differ;
				bestCoordinate = coo1;
			}
		}
		
		coordinate = bestCoordinate;
		
		return coordinate;
		
	}
	
	
	public static Coordinate calYkCoordinate(double m1, double n1, double l1, double m2, double n2, double l2, double m3, double n3, double l3)
	{
		Coordinate coordinate = new Coordinate();
		coordinate.setLatitude(0);
		coordinate.setLongitude(0);
		
		
		double a11, a12, a21, a22, b1, b2;
		
		a11=2*calAValue*calAValue*(m2-m1);
		a12=2*calBValue*calBValue*(n2-n1);
		a21=2*calAValue*calAValue*(m3-m1);
		a22=2*calBValue*calBValue*(n3-n1);
		b1=l1*l1-l2*l2+calAValue*calAValue*(m2*m2-m1*m1)+calBValue*calBValue*(n2*n2-n1*n1);
		b2=l1*l1-l3*l3+calAValue*calAValue*(m3*m3-m1*m1)+calBValue*calBValue*(n3*n3-n1*n1);
		
		double deno = a11*a22-a12*a21;
		double numerator_x =  b1*a22-b2*a12;
		double numerator_y =  b2*a11-b1*a21;
		
		double x = numerator_x / deno;
		double y = numerator_y / deno;
		
		if(deno==0) {
			return coordinate;
		} else {
			x = numerator_x / deno;
			y = numerator_y / deno;
		}
		
		if(x>0 && y>0) {
			if(Math.abs(x - m1) > 10 || Math.abs(x - m2) > 10 ||  Math.abs(x - m3) > 10 || Math.abs(y - n1) > 10 || Math.abs(y - n2) > 10 || Math.abs(y - n3) > 10)
			{
				return coordinate;
			} else {
				coordinate.setLatitude(x);
				coordinate.setLongitude(y);
				return coordinate;
			}
			
		} else {
			return coordinate;
		}
		
	}





private static Object cos(double sta) {
	// TODO Auto-generated method stub
	return null;
}

public static Coordinate beginCalculate(Set<LocationBean> resultXYSet){

	Coordinate resultCoordinate = new Coordinate();

	if(resultXYSet==null)
		return null;
	
	Coordinate coordinate = new Coordinate();
	
	LocationBean xy1 = new LocationBean();
	LocationBean xy2 = new LocationBean();
	LocationBean xy3 = new LocationBean();
	
	
	int i=0;
	int signal = resultXYSet.size();
	
	List<Coordinate> xyCoordinateList = new ArrayList<Coordinate>();
	
	for(LocationBean resultXYLBean : resultXYSet)
	{
		i++;
		
		
		if(i==1)
		{
			xy1 = resultXYLBean;
			continue;
		} else if(i==2) {
			xy2 = xy1;
			xy1 = resultXYLBean;
			continue;
		} else if(i>=3 && i <= signal) {
			xy3 = xy2;
			xy2 = xy1;
			xy1 = resultXYLBean;
			
			coordinate = calYYCoordinate(xy1.getLatitude(),xy1.getLongitude(),xy1.getDistance(),xy2.getLatitude(),xy2.getLongitude(),xy2.getDistance(), xy3.getLatitude(),xy3.getLongitude(),xy3.getDistance());		
			if(coordinate.getLatitude() != 0 && coordinate.getLongitude() != 0)
				xyCoordinateList.add(coordinate);	
			
			coordinate = calYYCoordinate(xy2.getLatitude(),xy2.getLongitude(),xy2.getDistance(),xy3.getLatitude(),xy3.getLongitude(),xy3.getDistance(),xy1.getLatitude(),xy1.getLongitude(),xy1.getDistance());		
			if(coordinate.getLatitude() != 0 && coordinate.getLongitude() != 0)
				xyCoordinateList.add(coordinate);
			
			coordinate = calYYCoordinate(xy3.getLatitude(),xy3.getLongitude(),xy3.getDistance(),xy2.getLatitude(),xy2.getLongitude(),xy2.getDistance(),xy1.getLatitude(),xy1.getLongitude(),xy1.getDistance());		
			if(coordinate.getLatitude() != 0 && coordinate.getLongitude() != 0)
				xyCoordinateList.add(coordinate);
			
		} else {
			break;
		}
		
		
	}

//	System.exit(0);
	
	int listSize = xyCoordinateList.size();
	
	if(listSize == 0)
		return resultCoordinate;
	
	
	Collections.sort(xyCoordinateList, new Comparator() {
	      @Override
	      public int compare(Object o1, Object o2) {
	    	  Coordinate coo1 = (Coordinate)o1;
	    	  Coordinate coo2 = (Coordinate)o2;
	          return new Double(coo1.getLatitude()).compareTo(new Double(coo2.getLatitude()));

	      }

	    });
	
	
	 
	double latMid = xyCoordinateList.get(listSize/2).getLatitude();
	resultCoordinate.setLatitude(latMid);
	
	
	Collections.sort(xyCoordinateList, new Comparator() {
	      @Override
	      public int compare(Object o1, Object o2) {
	    	  Coordinate coo1 = (Coordinate)o1;
	    	  Coordinate coo2 = (Coordinate)o2;
	          return new Double(coo1.getLongitude()).compareTo(new Double(coo2.getLongitude()));

	      }

	    });
	
   
	
	double lonMid = xyCoordinateList.get(listSize/2).getLongitude();
	resultCoordinate.setLongitude(lonMid);

	
	return resultCoordinate;
	

}




public static Coordinate beginCalculate2(Set<LocationBean> resultXYSet){

	Coordinate resultCoordinate = new Coordinate();

	if(resultXYSet==null)
		return null;
	
	Coordinate coordinate = new Coordinate();
	
	LocationBean xy1 = new LocationBean();
	LocationBean xy2 = new LocationBean();
	LocationBean xy3 = new LocationBean();
	
	
	int i=0;
	int signal = resultXYSet.size();
	
	List<Coordinate> xyCoordinateList = new ArrayList<Coordinate>();
	
	for(LocationBean resultXYLBean : resultXYSet)
	{
		i++;
		
		
		if(i==1)
		{
			xy1 = resultXYLBean;
			continue;
		} else if(i==2) {
			xy2 = xy1;
			xy1 = resultXYLBean;
			continue;
		} else if(i>=3 && i <= signal) {
			xy3 = xy2;
			xy2 = xy1;
			xy1 = resultXYLBean;
			
			coordinate = calYkCoordinate(xy1.getLatitude(),xy1.getLongitude(),xy1.getDistance(),xy2.getLatitude(),xy2.getLongitude(),xy2.getDistance(), xy3.getLatitude(),xy3.getLongitude(),xy3.getDistance());		
			if(coordinate.getLatitude() != 0 && coordinate.getLongitude() != 0)
				xyCoordinateList.add(coordinate);	
			
			coordinate = calYkCoordinate(xy2.getLatitude(),xy2.getLongitude(),xy2.getDistance(),xy3.getLatitude(),xy3.getLongitude(),xy3.getDistance(),xy1.getLatitude(),xy1.getLongitude(),xy1.getDistance());		
			if(coordinate.getLatitude() != 0 && coordinate.getLongitude() != 0)
				xyCoordinateList.add(coordinate);
			
			coordinate = calYkCoordinate(xy3.getLatitude(),xy3.getLongitude(),xy3.getDistance(),xy2.getLatitude(),xy2.getLongitude(),xy2.getDistance(),xy1.getLatitude(),xy1.getLongitude(),xy1.getDistance());		
			if(coordinate.getLatitude() != 0 && coordinate.getLongitude() != 0)
				xyCoordinateList.add(coordinate);
			
		} else {
			break;
		}
		
		
	}

//	System.exit(0);
	
	int listSize = xyCoordinateList.size();
	
	
	Collections.sort(xyCoordinateList, new Comparator() {
	      @Override
	      public int compare(Object o1, Object o2) {
	    	  Coordinate coo1 = (Coordinate)o1;
	    	  Coordinate coo2 = (Coordinate)o2;
	          return new Double(coo1.getLatitude()).compareTo(new Double(coo2.getLatitude()));

	      }

	    });
	
	
	 
	double latMid = xyCoordinateList.get(listSize/2).getLatitude();
	resultCoordinate.setLatitude(latMid);
	
	
	Collections.sort(xyCoordinateList, new Comparator() {
	      @Override
	      public int compare(Object o1, Object o2) {
	    	  Coordinate coo1 = (Coordinate)o1;
	    	  Coordinate coo2 = (Coordinate)o2;
	          return new Double(coo1.getLongitude()).compareTo(new Double(coo2.getLongitude()));

	      }

	    });
	
   
	
	double lonMid = xyCoordinateList.get(listSize/2).getLongitude();
	resultCoordinate.setLongitude(lonMid);

	
	return resultCoordinate;
	

}
	
}

