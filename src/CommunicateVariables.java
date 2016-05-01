import java.util.ArrayList;
import java.util.List;


public class CommunicateVariables {
	private boolean imageFinished = false;
	private boolean audioFinished = false;
	private int[] imageIndex;
	private int[] audioIndex;
	private static CommunicateVariables communicateVariables;
	public CommunicateVariables(){
		
	}
	
	public static CommunicateVariables getSingular(){
		if(communicateVariables == null){
			communicateVariables = new CommunicateVariables();
		}
		return communicateVariables;
	}
	public int[] trimRightZero(int[] index){
		
		List<Integer> temp = new ArrayList<>();
		if(index[0] == 0){
			temp.add(0);
		}
		for(int i = 0; i!= index.length; i++){
			if(index[i] != 0){
				temp.add(index[i]);
			}
		}
		int[] result = new int[temp.size()];
		for(int i = 0; i!= result.length; i++){
			result[i] = temp.get(i);
		}
		return result;
	}
	public void imageIndexInput(int[] imageIndex){
		this.imageIndex = trimRightZero(imageIndex);
		this.imageFinished = true;
	}
	public void audioIndexInput(int[] audioIndex){
		this.audioIndex = trimRightZero(audioIndex);
		this.audioFinished = true;
	}
	public boolean isImageFinished() {
		return imageFinished;
	}
	public boolean isAudioFinished() {
		return audioFinished;
	}
	public int[] getImageIndex() {
		return imageIndex;
	}
	public int[] getAudioIndex() {
		return audioIndex;
	}
	public boolean finished(){
		return audioFinished && imageFinished;
	}
	public synchronized int[] getIndex(){
		List<Integer> temp = new ArrayList<>();
		int i = 0;
		int j = 0;
		while(i < imageIndex.length || j < audioIndex.length){
			if(j >= audioIndex.length){
				if(temp.size() == 0 || temp.get(temp.size()-1) != imageIndex[i]){
					temp.add(imageIndex[i]);
				}
				i++;
			}
			else if(i >= imageIndex.length){
				if(temp.size() == 0 || temp.get(temp.size()-1) != audioIndex[j]){
					temp.add(audioIndex[j]);
				}
				j++;
			}
			else if(audioIndex[j] < imageIndex[i]){
				if(temp.size() == 0 || temp.get(temp.size()-1) != audioIndex[j]){
					temp.add(audioIndex[j]);
				}
				j++;
			}
			else {
				if(temp.size() == 0 || temp.get(temp.size()-1) != imageIndex[i]){
					temp.add(imageIndex[i]);
				}
				i++;
			}
			
			
		}
		int[] result = new int[temp.size()];
		System.out.println("audio");
		for(i = 0; i!= audioIndex.length; i++){
			System.out.println(audioIndex[i]);
		}
		System.out.println("image");
		for(i = 0; i!= imageIndex.length; i++){
			System.out.println(imageIndex[i]);
		}
		System.out.println("reslt");
		for(i = 0; i!= result.length; i++){
			result[i] = temp.get(i);
			System.out.println(result[i]);
		}
		return result;
	}
}
