package pos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class POSTagging {

	private HashMap<String,Integer> words = new HashMap <String,Integer>();
	private HashMap<String,Integer> tags = new HashMap <String,Integer>();
	private HashMap<String,HashMap<String,Integer>> State_Trans = new HashMap <String,HashMap<String,Integer>>();
	private HashMap<String,HashMap<String,Integer>> Obs_Prob=new HashMap <String,HashMap<String,Integer>>();

	public static void main(String[] args) {
		POSTagging pt = new POSTagging();
		//		String filePath = "C:\\Users\\AT\\Documents\\NLP\\entrain.txt";
		String filePath = "entrain.txt";



		try{
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			BufferedWriter out = new BufferedWriter(new FileWriter("out.txt"));
			// reader.close();

			String first;
			String second;
			//create file
			first=reader.readLine();
			String[] initial= first.split("/",2);
			pt.words.put(initial[0], 1);
			pt.tags.put(initial[1], 1);
			HashMap<String,Integer> temp1= new HashMap<String,Integer>();
			temp1.put(initial[1],1);
			pt.Obs_Prob.put(initial[0], temp1);
			while (first != null)
			{
				String line=null;
				while((line=reader.readLine())!=null && (!line.equalsIgnoreCase("###/###")))
				{
					second=line;
					String[] second_Line = second.split("/", 2); // split second line
					String word2= second_Line[0];   
					String tag2=second_Line[1];

					String[] first_Line = first.split("/", 2); // split first line
					//String word1= first_Line[0];   
					String tag1=first_Line[1];

					if(pt.words.containsKey(word2)){
						//            System.out.println(word);
						int count = pt.words.get(word2);
						count++;
						pt.words.put(word2, count);
					}else{
						//System.out.println(word);
						pt.words.put(word2, 1);

					}


					if(pt.tags.containsKey(tag2)){
						//            System.out.println(word);
						int count = pt.tags.get(tag2);
						count++;

						pt.tags.put(tag2, count);
					}else{
						//System.out.println(tag2);
						pt.tags.put(tag2, 1);

					}

					if(pt.State_Trans.containsKey(tag1))
					{
						HashMap<String,Integer> temp= pt.State_Trans.get(tag1);
						if(temp.containsKey(tag2)) {
							int count =temp.get(tag2);
							count++;
							temp.put(tag2,count);
							pt.State_Trans.put(tag1,temp);
						}else{
							temp.put(tag2,1);
							pt.State_Trans.put(tag1, temp);
						}
					}
					else {
						HashMap<String,Integer> temp= new HashMap<String,Integer>();
						temp.put(tag2,1);
						pt.State_Trans.put(tag1, temp);
					}

					if(pt.Obs_Prob.containsKey(word2)){
						HashMap<String,Integer> temp= pt.Obs_Prob.get(word2);
						if(temp.containsKey(tag2)){
							int count = temp.get(tag2);
							count++;
							temp.put(tag2,count);
							pt.Obs_Prob.put(word2,temp);
						}else{
							temp.put(tag2,1);
							pt.Obs_Prob.put(word2, temp);
						}
					}
					else {
						HashMap<String,Integer> temp= new HashMap<String,Integer>();
						temp.put(tag2,1);
						pt.Obs_Prob.put(word2, temp);
					}
					first=second;
				}				
				first = line;


			}



			reader.close();
			out.close();


		}catch(IOException e){
			e.printStackTrace();
		}

		/*for(String key : State_Trans.keySet()){
			System.out.println("Key: "+key);
			HashMap<String,Integer> m = State_Trans.get(key);
			for(String s : m.keySet()){
				System.out.println("Value: "+ s + "\t Count: "+ m.get(s));
			}

			System.out.println("**************************************");
			//	            if(key.startsWith("#")){
			//	            System.out.println("Key: "+key +"\t Value:"+ words.get(key));
//			out.append("Key: "+key +"\t Value:"+ words.get(key));
//			out.newLine();
			//	            }
		}

		 */
		//System.out.println(Obs_Prob.size());
		//System.out.println(pt.words.size());
		//System.out.println(tags.size());

		//	            if(key.startsWith("#")){
		//	            System.out.println("Key: "+key +"\t Value:"+ words.get(key));
		//			out.append("Key: "+key +"\t Value:"+ words.get(key));
		//			out.newLine();
		//	            }


		
		///viterbi impelemtation////
		//		String testPath = "C:\\Users\\AT\\Documents\\NLP\\entest.txt";
		String testPath = "entest.txt";


		try{
			BufferedReader reader = new BufferedReader(new FileReader(testPath));
			BufferedWriter out = new BufferedWriter(new FileWriter("out.txt"));
			// reader.close();
			int total_Tags=0;
			int total_Words=0;
			for(Map.Entry<String, Integer> entry : pt.words.entrySet()) {
				int i=entry.getValue();
				total_Words=total_Words+i;

			}
			String f;
			f=reader.readLine();
			//			String[] initial= f.split("/",2);
			//			String w1=initial[0];
			HashMap<Integer,ArrayList<String>> tag_Sequence = new HashMap<Integer,ArrayList<String>> ();
			int s_No=0;
			while (f != null)
			{
				ArrayList<String> listOfWordsInASent = new ArrayList<String>();
				s_No++;
				String[] initial= f.split("/",2);
				String w1=initial[0];	
				String t1=initial[1];

				//				HashMap<String,String> temp= new HashMap<String,String>();
				//				temp.put(w1,t1 );
				//				tag_Sequence.put(s_No,temp);

				listOfWordsInASent.add(w1);

				String line=null;
				while((line=reader.readLine())!=null && (!line.equalsIgnoreCase("###/###")))			
				{
					String[] second_Line = line.split("/"); // split second line
					String tmpWord = second_Line[0];

					listOfWordsInASent.add(tmpWord);
				}	

				ArrayList<String> mapOfTags = pt.viterbi(listOfWordsInASent);
				for(String word: mapOfTags){
					out.append(word);
					out.newLine();
				}
				tag_Sequence.put(s_No, mapOfTags);
				f = line;
			}

			reader.close();
			out.close();
		}catch(IOException e){
			e.printStackTrace();
		}



		double ac = pt.getAccuracy();
		
		//System.out.println("Accuracy is: "+ac);
		double err=1-ac;
		System.out.println("Error Rate: "+err);

	}


	private double getAccuracy(){
		double correct = 0;
		double total = 0;
		try(BufferedReader br1 = new BufferedReader(new FileReader("entest.txt"));
				BufferedReader br2 = new BufferedReader(new FileReader("out.txt"))){
			String line1 = null;
			String line2 = null;
			while((line1=br1.readLine())!=null){
				line2 = br2.readLine();	
				if(line1.equalsIgnoreCase(line2)){
					correct++;
				}
				total++;
			}
		}catch(IOException e){
			e.printStackTrace();
		}

		return (correct/total);
	}




	private ArrayList<String> viterbi(ArrayList<String> listOfWords){

		ArrayList<String> t = new ArrayList<String>();
		t.add("###/###");
		// List<String> strings = new ArrayList<>();


		String finalTag="###";
		for(String word: listOfWords)
		{
			if(word.equalsIgnoreCase("###")) {
				continue;
			}

			double maxProb=0;
			for(String tag : tags.keySet()) {
				double emission_Prob=0;
				double state_Transition_Prob=0;

				double emission_Count_Numerator=0;
				double emission_Count_Denominator=0;
				double state_Transition_Count_Numerator=0;
				double state_Transition_Count_Denominator=0;

				if(Obs_Prob.containsKey(word)) {
					HashMap<String,Integer> temp= Obs_Prob.get(word);
					if(temp.containsKey(tag))
					{
						emission_Count_Numerator=temp.get(tag); 
					}else{
						emission_Count_Numerator=0;
					}
				}else{
					emission_Count_Numerator=0;
				}

				emission_Count_Denominator=tags.get(tag);
				emission_Prob=(emission_Count_Numerator)/(emission_Count_Denominator);


				if(State_Trans.containsKey(finalTag)) {
					HashMap<String,Integer> temp= State_Trans.get(finalTag);
					if(temp.containsKey(tag))
					{
						state_Transition_Count_Numerator=temp.get(tag); 
					}
					else{
						state_Transition_Count_Numerator=0;
					}
				}
				else{
					state_Transition_Count_Numerator=0;
				}


				state_Transition_Count_Denominator=tags.get(finalTag);
				state_Transition_Prob=(state_Transition_Count_Numerator)/(state_Transition_Count_Denominator);
				double final_Prob=state_Transition_Prob*emission_Prob;
				if(maxProb<final_Prob)
				{
					maxProb=final_Prob;
					finalTag=tag;
				}

			}
			t.add(word+"/"+finalTag);

		}



		return t;
	}
	//int i=entry.getValue();

}		







