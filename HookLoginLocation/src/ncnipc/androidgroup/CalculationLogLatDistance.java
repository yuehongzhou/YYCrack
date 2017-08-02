package ncnipc.androidgroup;


/**
 * 
 * ���㾭γ�ȡ����롢��λ��
 * 
 * @author lillian.he
 * @time 2016-06-02
 * */
public class CalculationLogLatDistance {
    /**
     * �������뾶(km)
     * */
    public final static double EARTH_RADIUS = 6378.137;
    /**
     * ����ÿ�ȵĻ���(km)
     * */
    public final static double EARTH_ARC = 111.199;

    /**
     * ת��Ϊ����(rad)
     * */
    public static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * ������γ�Ⱦ���
     * 
     * @param lon1
     *            ��һ��ľ���
     * @param lat1
     *            ��һ���γ��
     * @param lon2
     *            �ڶ���ľ���
     * @param lat2
     *            �ڶ����γ��
     * @return ������룬��λkm
     * */
    public static double GetDistanceOne(double lon1, double lat1, double lon2,
            double lat2) {
        double r1 = rad(lat1);
        double r2 = rad(lon1);
        double a = rad(lat2);
        double b = rad(lon2);
        double s = Math.acos(Math.cos(r1) * Math.cos(a) * Math.cos(r2 - b)
                + Math.sin(r1) * Math.sin(a))
                * EARTH_RADIUS;
        return s;
    }

    /**
     * ������γ�Ⱦ���(google mapsԴ����)
     * 
     * @param lon1
     *            ��һ��ľ���
     * @param lat1
     *            ��һ���γ��
     * @param lon2
     *            �ڶ���ľ���
     * @param lat2
     *            �ڶ����γ��
     * @return ������룬��λkm
     * */
    public static double GetDistanceTwo(double lon1, double lat1, double lon2,
            double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        return s;
    }

    /**
     * ������γ�Ⱦ���
     * 
     * @param lon1
     *            ��һ��ľ���
     * @param lat1
     *            ��һ���γ��
     * @param lon2
     *            �ڶ���ľ���
     * @param lat2
     *            �ڶ����γ��
     * @return ������룬��λkm
     * */
    public static double GetDistanceThree(double lon1, double lat1,
            double lon2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double radLon1 = rad(lon1);
        double radLon2 = rad(lon2);
        if (radLat1 < 0)
            radLat1 = Math.PI / 2 + Math.abs(radLat1);// south
        if (radLat1 > 0)
            radLat1 = Math.PI / 2 - Math.abs(radLat1);// north
        if (radLon1 < 0)
            radLon1 = Math.PI * 2 - Math.abs(radLon1);// west
        if (radLat2 < 0)
            radLat2 = Math.PI / 2 + Math.abs(radLat2);// south
        if (radLat2 > 0)
            radLat2 = Math.PI / 2 - Math.abs(radLat2);// north
        if (radLon2 < 0)
            radLon2 = Math.PI * 2 - Math.abs(radLon2);// west
        double x1 = Math.cos(radLon1) * Math.sin(radLat1);
        double y1 = Math.sin(radLon1) * Math.sin(radLat1);
        double z1 = Math.cos(radLat1);

        double x2 = Math.cos(radLon2) * Math.sin(radLat2);
        double y2 = Math.sin(radLon2) * Math.sin(radLat2);
        double z2 = Math.cos(radLat2);

        double d = Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2)
                + Math.pow((z1 - z2), 2);
        // // ���Ҷ�����н�
        // double theta = Math.acos((2 - d) / 2);

        d = Math.pow(EARTH_RADIUS, 2) * d;
        // //���Ҷ�����н�
        double theta = Math.acos((2 * Math.pow(EARTH_RADIUS, 2) - d)
                / (2 * Math.pow(EARTH_RADIUS, 2)));

        double dist = theta * EARTH_RADIUS;
        return dist;
    }

    /**
     * ������γ�ȷ����
     * 
     * @param lon1
     *            ��һ��ľ���
     * @param lat1
     *            ��һ���γ��
     * @param lon2
     *            �ڶ���ľ���
     * @param lat2
     *            �ڶ����γ��
     * @return ��λ�ǣ��Ƕȣ���λ���㣩
     * */
    public static double GetAzimuth(double lon1, double lat1, double lon2,
            double lat2) {
        lat1 = rad(lat1);
        lat2 = rad(lat2);
        lon1 = rad(lon1);
        lon2 = rad(lon2);
        double azimuth = Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1)
                * Math.cos(lat2) * Math.cos(lon2 - lon1);
        azimuth = Math.sqrt(1 - azimuth * azimuth);
        azimuth = Math.cos(lat2) * Math.sin(lon2 - lon1) / azimuth;
        azimuth = Math.asin(azimuth) * 180 / Math.PI;
        if (Double.isNaN(azimuth)) {
            if (lon1 < lon2) {
                azimuth = 90.0;
            } else {
                azimuth = 270.0;
            }
        }
        return azimuth;
    }

    /**
     * ��֪һ�㾭γ��A��������һ��B�ľ���ͷ�λ�ǣ���B�ľ�γ��(����������)
     * 
     * @param lon1
     *            A�ľ���
     * @param lat1
     *            A��γ��
     * @param distance
     *            AB���루��λ���ף�
     * @param azimuth
     *            AB��λ��
     * @return B�ľ�γ��
     * */
    public static String GetOtherPoint(double lon1, double lat1,
            double distance, double azimuth) {
        azimuth = rad(azimuth);
        double ab = distance / EARTH_ARC;// AB�仡�߳�
        ab = rad(ab);
        double Lat = Math.asin(Math.sin(lat1) * Math.cos(ab) + Math.cos(lat1)
                * Math.sin(ab) * Math.cos(azimuth));
        double Lon = lon1
                + Math.asin(Math.sin(azimuth) * Math.sin(ab) / Math.cos(Lat));
        System.out.println(Lon + "," + Lat);

        double a = Math.acos(Math.cos(90 - lon1) * Math.cos(ab)
                + Math.sin(90 - lon1) * Math.sin(ab) * Math.cos(azimuth));
        double C = Math.asin(Math.sin(ab) * Math.sin(azimuth) / Math.sin(a));
        System.out.println("c=" + C);
        double lon2 = lon1 + C;
        double lat2 = 90 - a;
        return lon2 + "," + lat2;
    }

    /**
     * ��֪һ�㾭γ��A��������һ��B�ľ���ͷ�λ�ǣ���B�ľ�γ��
     * 
     * @param lon1
     *            A�ľ���
     * @param lat1
     *            A��γ��
     * @param distance
     *            AB���루��λ���ף�
     * @param azimuth
     *            AB��λ��
     * @return B�ľ�γ��
     * */
    public static String ConvertDistanceToLogLat(double lng1, double lat1,
            double distance, double azimuth) {
        azimuth = rad(azimuth);
        // ������ת���ɾ��ȵļ��㹫ʽ
        double lon = lng1 + (distance * Math.sin(azimuth))
                / (EARTH_ARC * Math.cos(rad(lat1)));
        // ������ת����γ�ȵļ��㹫ʽ
        double lat = lat1 + (distance * Math.cos(azimuth)) / EARTH_ARC;
        return lon + "," + lat;
    }
    
    
    private static double gps2d(double lat_a, double lng_a, double lat_b, double lng_b){
    	  double d = 0;
    	  lat_a=lat_a*Math.PI/180;
    	  lng_a=lng_a*Math.PI/180;
    	  lat_b=lat_b*Math.PI/180;
    	  lng_b=lng_b*Math.PI/180;
    	         
    	  d=Math.sin(lat_a)*Math.sin(lat_b)+Math.cos(lat_a)*Math.cos(lat_b)*Math.cos(lng_b-lng_a);
    	  d=Math.sqrt(1-d*d);
    	  d=Math.cos(lat_b)*Math.sin(lng_b-lng_a)/d;
    	  d=Math.asin(d)*180/Math.PI;
    	//d = Math.round(d*10000);
    	  return d;
    	}
    
    
    private static double angleFromCoordinate(double lat1, double long1, double lat2,
            double long2) {

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng; // count degrees counter-clockwise - remove to make clockwise

        return brng;
    }
    
    
    static public double initial_ (double lat1, double long1, double lat2, double long2)
    {
        return (_bearing(lat1, long1, lat2, long2) + 360.0) % 360;
    }

    static public double final_ (double lat1, double long1, double lat2, double long2)
    {
        return (_bearing(lat2, long2, lat1, long1) + 180.0) % 360;
    }

    static private double _bearing(double lat1, double long1, double lat2, double long2)
    {
        double degToRad = Math.PI / 180.0;
        double phi1 = lat1 * degToRad;
        double phi2 = lat2 * degToRad;
        double lam1 = long1 * degToRad;
        double lam2 = long2 * degToRad;

        return Math.atan2(Math.sin(lam2-lam1)*Math.cos(phi2),
            Math.cos(phi1)*Math.sin(phi2) - Math.sin(phi1)*Math.cos(phi2)*Math.cos(lam2-lam1)
        ) * 180/Math.PI;
    }
    
    
    protected static double getAzimuth2(double lat1, double lon1, double lat2, double lon2){
    	  double longitude1 = lon1;
    	  double longitude2 = lon2;
    	  double latitude1 = Math.toRadians(lat1);
    	  double latitude2 = Math.toRadians(lat2);
    	  double longDiff= Math.toRadians(longitude2-longitude1);
    	  double y= Math.sin(longDiff)*Math.cos(latitude2);
    	  double x=Math.cos(latitude1)*Math.sin(latitude2)-Math.sin(latitude1)*Math.cos(latitude2)*Math.cos(longDiff);

    	  return (Math.toDegrees(Math.atan2(y, x))+360)%360;
    	}
    
    
    protected static double getAzimuth3(double lat1, double lon1, double lat2, double lon2){
      
    	double midLat = (lat1 + lat2) / 2;
    	double radius = MapUtils.getEARTH_RADIUS() * Math.abs(Math.cos(midLat * Math.PI / (double)180));
		double lc = radius * Math.PI * 2;
		double calBValue = lc / (double)360;
		
		MapUtils.setCalBValue(calBValue);
		
      double a = MapUtils.getCalAValue();
      double b = MapUtils.getCalBValue();
      
  	  double tanS1_zi = Math.abs(lat1-lat2)*a;
  	  double tanS1_mu = Math.abs(lon1-lon2)*b;
  	  
  	  if(tanS1_mu == 0)
  		  return 90.0;
  	  
  	  double tanS1 = tanS1_zi / tanS1_mu;
  	  return (Math.toDegrees(Math.atan(tanS1))+360)%360;
  	  
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
    
    

    public static void main(String[] args) {
    	
    	
    	double degrees = 315.0;
		double radians = Math.toRadians(degrees);

		System.out.format("pi ��ֵΪ %.4f%n", Math.PI);
		System.out.format("%.4f �ķ�����ֵΪ %.4f �� %n", Math.cos(radians), Math.toDegrees(Math.acos(Math.cos(radians))));
		
		
        double lon1 = 80.5;
        double lat1 = 30;
        double lon2 = 80.5;
        double lat2 = 29.2;
        double distance = GetDistanceTwo(lon1, lat1, lon2, lat2);
        
        double azimuth = 0;
        		
         azimuth = getAzimuth3(lon1, lat1, lon2, lat2);
         azimuth = calAngleBaseLat(azimuth);
        
//        double azimuth = gps2d(lat1, lon1, lat2, lon2);
        
//        double azimuth = angleFromCoordinate(lat1, lon1, lat2, lon2);
        
          azimuth = getAzimuth2(lat1, lon1, lat2, lon2);
          azimuth = calAngleBaseLat(azimuth);
        
        
        
        System.out.println("��γ��Ϊ(" + lon1 + "," + lat1 + ")�ĵ��뾭γ��Ϊ(" + lon2
                + "," + lat2 + ")��ࣺ" + distance + "ǧ��," + "��λ�ǣ�" + azimuth
                + "��");
        
        
        System.out.println("�ྭγ��Ϊ(" + lon1 + "," + lat1 + ")�ĵ�" + distance
                + "ǧ��,��λ��Ϊ" + azimuth + "�����һ�㾭γ��Ϊ("
                + ConvertDistanceToLogLat(lon1, lat1, distance, azimuth) + ")");
    }
}