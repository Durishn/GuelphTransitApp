import java.util.ArrayList;
import java.util.List;


//Routes have names
public class Route{
	private String routeName;
	//routes have lists of stops
	private ArrayList<Stop> stopList;

	public Route(String routeName,ArrayList<Stop> stopListInput){
		this.routeName = routeName;
		stopList= new ArrayList<Stop>();
		for(Stop s:stopListInput){
			this.stopList.add(new Stop(s.getStopName(),s.getStopID(),
				s.getWeekTimes(),s.getSatTimes(),s.getSunTimes(),
				s.getLatitude(),s.getLongitutde()));
		}
	}
	//toString for 
	@Override 
	public String toString(){
		String s="";
		//Route name first
		s+=routeName +" ";
		for (Stop st:stopList){
			//Every Stop is seperated by an " xx ""
			if(st!=null){
				s+=st.toString();
			}else{
				//throw exception?
			}
		}
		//Every route is seperated on each line
		s+='\n'; 
		return s;
	}


}
