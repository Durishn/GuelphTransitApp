import java.util.ArrayList;
import java.util.List;

//Stops have a stop name
//Stop ID
//times for that stop
public class Stop{
	private String stopName;
	private String id;
	private ArrayList<String> weekTimes;
	private ArrayList<String> satTimes;
	private ArrayList<String> sunTimes;
	private float latitude;
	private float longitude;

	public Stop(String stopName,String id,ArrayList<String> weekTimesInput,ArrayList<String> satTimesInput,ArrayList<String> sunTimesInput,float latIn,float longIn){
		this.weekTimes = new ArrayList<String>();
		this.satTimes  = new ArrayList<String>();
		this.sunTimes = new ArrayList<String>();

		this.latitude = latIn;
		this.longitude = longIn;

		this.stopName=stopName;
		this.id = id;
		for(String s :weekTimesInput){
			this.weekTimes.add(new String(s));
		}
		for(String s:satTimesInput){
			this.satTimes.add(new String(s));
		}
		for(String s:sunTimesInput){
			this.sunTimes.add(new String(s));
		}
	}
	@Override
	public String toString(){
		String s="";
		s+=this.stopName+" ";
		s+=this.id+" ";

		for (String t:this.weekTimes){
			if(t!=null){
				s+=t+" ";
			}else{
				//throw exception?
			}
		}
		//Every stop is seperated by an xx
		s+=" xx ";
		return s;
	}

	public String getStopName(){
		return this.stopName;
	}
	public String getStopID(){
		return this.id;
	}
	public ArrayList<String> getWeekTimes(){
		return this.weekTimes;
	}
	public ArrayList<String> getSatTimes(){
		return this.satTimes;
	}
	public ArrayList<String> getSunTimes(){
		return this.sunTimes;
	}
	public float getLatitude(){
		return this.latitude;
	}
	public float getLongitude(){
		return this.longitude;
	}

}