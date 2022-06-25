import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrices;
import no.uib.cipr.matrix.NotConvergedException;

public class ELM_process_hybrid {
	private static final Exception IOexception = null;
	private DenseMatrix train_set;
	private DenseMatrix test_set;
	private int numTrainData;
	private int numTestData;
	private DenseMatrix InputWeight;
	private float TrainingTime;
	private float TestingTime;
	private double TrainingAccuracy, TestingAccuracy;
	private int Elm_Type;
	private int NumberofHiddenNeurons;
	private int NumberofOutputNeurons;						//also the number of classes
	private int NumberofInputNeurons;						//also the number of attribution
	private String func;
	private int []label= {0,1,2,3,4,5};		
	//this class label employ a lazy and easy method,any class must written in 0,1,2...so the preprocessing is required
	
	//the blow variables in both train() and test()
	private DenseMatrix  BiasofHiddenNeurons;
	private DenseMatrix  OutputWeight;
	private DenseMatrix  testP;
	private DenseMatrix  testT;
	private DenseMatrix  Y;
	private DenseMatrix  T;

	/**
     * Construct an ELM
     * @param
     * ELM_process_type              - 0 for regression; 1 for (both binary and multi-classes) classification
     * @param
     * numberofHiddenNeurons - Number of hidden neurons assigned to the ELM
     * @param
     * ActivationFunction    - Type of activation function:
     *                      'sig' for Sigmoidal function
     *                      'sin' for Sine function
     *                      'hardlim' for Hardlim function
     *                      'tribas' for Triangular basis function
     *                      'radbas' for Radial basis function (for additive type of SLFNs instead of RBF type of SLFNs)
     * @throws NotConvergedException
     */
	
	public ELM_process_hybrid(int ELM_process_type, int numberofHiddenNeurons, String ActivationFunction){
		
		
		
		Elm_Type = ELM_process_type;
		NumberofHiddenNeurons = numberofHiddenNeurons;
		func = ActivationFunction;
		
		TrainingTime = 0;
		TestingTime = 0;
		TrainingAccuracy= 0;
		TestingAccuracy = 0;
		NumberofOutputNeurons = 6;	
		
	}
	public ELM_process_hybrid(){
		
	}

	public DenseMatrix loadmatrix(String filename) throws IOException{
		

		BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));

		String line = "";

		CSVReader cv = new CSVReader();
	    int m=0;
	    int n=16;
	
		m=cv.findM(filename);

		NumberofOutputNeurons = 6;//0,1,2,3,4,5
		 
        String cvsSplitBy = ",";
        int i = 0;
       
        double tempt=0;
        String[] datatrings  = {"0","1","2","3","4","5"};
		DenseMatrix matrix = new DenseMatrix(m, n);
        
		 while ((line = reader.readLine()) != null) {
                // use comma as separator

                  datatrings = line.split(cvsSplitBy);		

          for(int j=0;j<n;j++) {

            	tempt = Math.ceil(Double.parseDouble(datatrings[j]));            	
				matrix.set(i, j, tempt);
	
			}
        i++;


            }
            


			return matrix;

	}
	
	
	public void train(String TrainingData_File) throws NotConvergedException{
		try {
			train_set = loadmatrix(TrainingData_File);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		train();
	}
	
	public void train(double [][]traindata) throws NotConvergedException{
	
		//classification require a the number of class
		
		train_set = new DenseMatrix(traindata);
		int m = train_set.numRows();
		if(Elm_Type == 1){
			double maxtag = traindata[0][0];
			for (int i = 0; i < m; i++) {
				if(traindata[i][0] > maxtag)
					maxtag = traindata[i][0];
			}
		    NumberofOutputNeurons = (int)maxtag+1;
		
		}
		train();
	}
	
	
	private void train() throws NotConvergedException{
		
		numTrainData = train_set.numRows();
		NumberofInputNeurons = train_set.numColumns() - 1;
		InputWeight = (DenseMatrix) Matrices.random(NumberofHiddenNeurons, NumberofInputNeurons);
		
		DenseMatrix transT = new DenseMatrix(numTrainData, 1);
		DenseMatrix transP = new DenseMatrix(numTrainData, NumberofInputNeurons);
		for (int i = 0; i < numTrainData; i++) {
			transT.set(i, 0, train_set.get(i, 0));
			for (int j = 0; j < NumberofInputNeurons; j++)
				transP.set(i, j, train_set.get(i, j));
		}
		T = new DenseMatrix(1,numTrainData);
		DenseMatrix P = new DenseMatrix(NumberofInputNeurons,numTrainData);
		transT.transpose(T);
		transP.transpose(P);
		
		if(Elm_Type != 0)	//CLASSIFIER
		{
			label = new int[NumberofOutputNeurons];
			for (int i = 0; i < NumberofOutputNeurons; i++) {
				label[i] = i;		//class label starts form 0
			}
			DenseMatrix tempT = new DenseMatrix(NumberofOutputNeurons,numTrainData);
			tempT.zero();
			for (int i = 0; i < numTrainData; i++){
					int j = 0;
			        for (j = 0; j < NumberofOutputNeurons; j++){
			            if (label[j] == T.get(0, i)) {
			            	tempT.set(j, i, j); 
			            }
			            
			        }
			    	
			}
			
			T = new DenseMatrix(NumberofOutputNeurons,numTrainData);	// T=temp_T*2-1;
			for (int i = 0; i < NumberofOutputNeurons; i++){
		        for (int j = 0; j < numTrainData; j++) {
		        
		        	if(i==0) {
	        		T.set(i, j, tempT.get(i, j));
		        	}else {
		        		T.set(i, j, tempT.get(i, j)*2-1);
		        		
		        	}
		        }
			}
			
			transT = new DenseMatrix(numTrainData,NumberofOutputNeurons);
			T.transpose(transT);
			
		} 	//end if CLASSIFIER
		
		long start_time_train = System.currentTimeMillis();
		// Random generate input weights InputWeight (w_i) and biases BiasofHiddenNeurons (b_i) of hidden neurons
		// InputWeight=rand(NumberofHiddenNeurons,NumberofInputNeurons)*2-1;
		
		BiasofHiddenNeurons = (DenseMatrix) Matrices.random(NumberofHiddenNeurons, 1);
		
		DenseMatrix tempH = new DenseMatrix(NumberofHiddenNeurons, numTrainData);
		InputWeight.mult(P, tempH);
		//DenseMatrix ind = new DenseMatrix(1, numTrainData);
		
		DenseMatrix BiasMatrix = new DenseMatrix(NumberofHiddenNeurons, numTrainData);
		
		for (int j = 0; j < numTrainData; j++) {
			for (int i = 0; i < NumberofHiddenNeurons; i++) {
				BiasMatrix.set(i, j, BiasofHiddenNeurons.get(i, 0));
			}
		}
	
		tempH.add(BiasMatrix);
		DenseMatrix H = new DenseMatrix(NumberofHiddenNeurons, numTrainData);
		
		if(func.startsWith("sig")){
			for (int j = 0; j < NumberofHiddenNeurons; j++) {
				for (int i = 0; i < numTrainData; i++) {
					double temp = tempH.get(j, i);
					temp = 1.0f/ (1 + Math.exp(-temp));
					H.set(j, i, temp);
				}
			}
		}
		else if(func.startsWith("sin")){
			for (int j = 0; j < NumberofHiddenNeurons; j++) {
				for (int i = 0; i < numTrainData; i++) {
					double temp = tempH.get(j, i);
					temp = Math.sin(temp);
					H.set(j, i, temp);
				}
			}
		}
		else if(func.startsWith("hardlim")){
			//If you need it ,you can absolutely complete it yourself
		}
		else if(func.startsWith("tribas")){
			//If you need it ,you can absolutely complete it yourself	
		}
		else if(func.startsWith("radbas")){
			//If you need it ,you can absolutely complete it yourself
		}

		DenseMatrix Ht = new DenseMatrix(numTrainData,NumberofHiddenNeurons);
		H.transpose(Ht);
		Inverse_Matrix invers = new Inverse_Matrix(Ht);
		DenseMatrix pinvHt = invers.getMPInverse_Matrix();			//NumberofHiddenNeurons*numTrainData

		OutputWeight = new DenseMatrix(NumberofHiddenNeurons, NumberofOutputNeurons);
		//OutputWeight=pinv(H') * T';  
		pinvHt.mult(transT, OutputWeight);
		
		long end_time_train = System.currentTimeMillis();
		TrainingTime = (end_time_train - start_time_train)*1.0f/1000;
		
		DenseMatrix Yt = new DenseMatrix(numTrainData,NumberofOutputNeurons);
		Ht.mult(OutputWeight,Yt);
		Y = new DenseMatrix(NumberofOutputNeurons,numTrainData);
		Yt.transpose(Y);
		
		if(Elm_Type == 0){
			double MSE = 0;
			for (int i = 0; i < numTrainData; i++) {
				MSE += (Yt.get(i, 0) - transT.get(i, 0))*(Yt.get(i, 0) - transT.get(i, 0));
			}
			TrainingAccuracy = Math.sqrt(MSE/numTrainData);
		}
		
		//CLASSIFIER
		else if(Elm_Type == 1){
			float MissClassificationRate_Training=0;
		    
		    for (int i = 0; i < numTrainData; i++) {
				double maxtag1 = Y.get(0, i);
				int tag1 = 0;
				double maxtag2 = T.get(0, i);
				int tag2 = 0;
				int sum=0;
				
		    	for (int j = 0; j < NumberofOutputNeurons; j++) {
					if(Y.get(j, i) > maxtag1){
						maxtag1 = Y.get(j, i);
						tag1 = j;
						sum=sum+tag1;
					}
					if(T.get(j, i) > maxtag2){
						maxtag2 = T.get(j, i);
						tag2 = j;
					}
				}
		    	if(tag1 != tag2) {
		    		MissClassificationRate_Training ++;
		    	}
			}
		    double TrainingAccuracy1;
		    TrainingAccuracy1 = 1 - MissClassificationRate_Training*1.0f/numTrainData;
		    TrainingAccuracy=TrainingAccuracy1*100;
			
		}
		
	}
	
	/**
	 * @param TestingData_File
	 * @throws IOException
	 */
	public void test(String TestingData_File) throws IOException{
		float missn=0;
		int count=6;
		
		try {
			test_set = loadmatrix(TestingData_File);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		numTestData = test_set.numRows();
		DenseMatrix ttestT = new DenseMatrix(numTestData, 1);
		DenseMatrix ttestP = new DenseMatrix(numTestData, NumberofInputNeurons);
		for (int i = 0; i < numTestData; i++) {
			ttestT.set(i, 0, test_set.get(i, 0));
			for (int j = 0; j < NumberofInputNeurons; j++) {
				ttestP.set(i, j, test_set.get(i, j));
			
			
			}
			
		}
		
		testT = new DenseMatrix(1,numTestData);
		testP = new DenseMatrix(NumberofInputNeurons,numTestData);
		ttestT.transpose(testT);
		ttestP.transpose(testP);
		
		long start_time_test = System.currentTimeMillis();
		DenseMatrix tempH_test = new DenseMatrix(NumberofHiddenNeurons, numTestData);
		InputWeight.mult(testP, tempH_test);
		DenseMatrix BiasMatrix2 = new DenseMatrix(NumberofHiddenNeurons, numTestData);
		
		for (int j = 0; j < numTestData; j++) {
			for (int i = 0; i < NumberofHiddenNeurons; i++) {
				BiasMatrix2.set(i, j, BiasofHiddenNeurons.get(i, 0));
			}
		}
	
		tempH_test.add(BiasMatrix2);
		DenseMatrix H_test = new DenseMatrix(NumberofHiddenNeurons, numTestData);
		
		if(func.startsWith("sig")){
			for (int j = 0; j < NumberofHiddenNeurons; j++) {
				for (int i = 0; i < numTestData; i++) {
					double temp = tempH_test.get(j, i);
					temp = 1.0f/ (1 + Math.exp(-temp));
					H_test.set(j, i, temp);
				}
			}
		}
		else if(func.startsWith("sin")){
			for (int j = 0; j < NumberofHiddenNeurons; j++) {
				for (int i = 0; i < numTestData; i++) {
					double temp = tempH_test.get(j, i);
					temp = Math.sin(temp);
					H_test.set(j, i, temp);
				}
			}
		}
		else if(func.startsWith("hardlim")){
			
		}
		else if(func.startsWith("tribas")){
	
		}
		else if(func.startsWith("radbas")){
			
		}
		
		DenseMatrix transH_test = new DenseMatrix(numTestData,NumberofHiddenNeurons);
		H_test.transpose(transH_test);
		DenseMatrix Yout = new DenseMatrix(numTestData,NumberofOutputNeurons);
		transH_test.mult(OutputWeight,Yout);
		
		DenseMatrix testY = new DenseMatrix(NumberofOutputNeurons,numTestData);
		Yout.transpose(testY);
		
		long end_time_test = System.currentTimeMillis();
		TestingTime = (end_time_test - start_time_test)*1.0f/1000;
		
		//REGRESSION
		if(Elm_Type == 0){
			double MSE = 0;
			for (int i = 0; i < numTestData; i++) {
				MSE += (Yout.get(i, 0) - testT.get(0,i))*(Yout.get(i, 0) - testT.get(0,i));
			}
			TestingAccuracy = Math.sqrt(MSE/numTestData);
		}
		
		
		//CLASSIFIER
		else if(Elm_Type == 1){

			DenseMatrix temptestT = new DenseMatrix(NumberofOutputNeurons,numTestData);
			DenseMatrix result_Y = new DenseMatrix(numTestData,NumberofOutputNeurons);
			DenseMatrix result_T = new DenseMatrix(numTestData,NumberofOutputNeurons);
			temptestT.zero();
			for (int i = 0; i < numTestData; i++){
					int j = 0;
			        for (j = 0; j < NumberofOutputNeurons; j++){
			            if (label[j] == testT.get(0, i)) {
			            	 temptestT.set(j, i, j); 
			            
			            }
			                
			        }
			        
			      
			     
			}
			DenseMatrix actT = new DenseMatrix(NumberofOutputNeurons,numTestData);
			testT = new DenseMatrix(NumberofOutputNeurons,numTestData);	
			for (int i = 0; i < NumberofOutputNeurons; i++){
		        for (int j = 0; j < numTestData; j++) {
		        	if(i==0) {
		        		testT.set(i, j, temptestT.get(i, j));
		        		actT.set(i,j,temptestT.get(i, j));
		        	
		        	}
		        	else {
		        		testT.set(i, j, temptestT.get(i, j)*2-1);
		        		
			            actT.set(i,j,temptestT.get(i, j)*2-1);
			           
		        	}
		        	
		        }
		       
			}
  
			String pathT = "src/T.txt";
			String pathY = "src/Y.txt";
			
			File fileT = new File(pathT);
			File fileY = new File(pathY);	
			
			FileWriter writerT = new FileWriter(fileT, true);  //True = Append to file, false = Overwrite
			FileWriter writerY = new FileWriter(fileY, true);
			
			
            String xml_Y="";
            String xml_T="";
            
             String value1="",value2="";
             writerY.write("<Results>");
		     writerT.write("<Results>");
             
		    float MissClassificationRate_Testing=0;
		    float RightClassificationRate_Testing=0;
		    
			System.out.println("Target\tPrediction ELM");

//            fileWriters ff = new fileWriters();
            
		    for (int i = 0; i < numTestData; i++) {
				double maxtag1 = testY.get(0, i);
				int tag1 = 0;
				
				
				
				double maxtag2 = testT.get(0, i);
				int tag2 = 0;
				
                int sum=0;
               
                
		    	for (int j = 0; j < NumberofOutputNeurons; j++) {
		    	
					if(testY.get(j, i) > maxtag1){
						maxtag1 = testY.get(j, i);		
						
						tag1 = j;
						sum=sum+tag1;
						
					
				
					}
				
					if(testT.get(j, i) > maxtag2){
						maxtag2 = testT.get(j, i);
						tag2 = j;
						
					
					}
					
					
					
					//System.out.println(i+"-"+j+" = "+actT.get(j,i)+":     "+tag2 + "\t\t" + tag1);

		    		
		    		
		    		
		    	    
		    	

					
				}
		    	 
		    	/*
		    	if(tag1 != tag2) {
		    		MissClassificationRate_Testing ++;
		    		//resultM.set(i,0,tag2);
		    		//resultM.set(i,1,tag1);
		    		

		    	}else */
		    		
		    		if(tag1!=tag2){
		    			if(i==59) {
		    				 System.out.println(i+" --------------------------------->"+tag1);
		    			}
		    			else {
		    				MissClassificationRate_Testing++;
		    				 System.out.println(i+" "+tag2+"------------ != ---------------->"+tag1);
		    			}
		    			
		    			
		    			
		    		//RightClassificationRate_Testing++;		    		
		    		
		    	  }else {
		    		  System.out.println(" "+tag2+"------------ == ---------------->"+tag1);
		    		
			    
		    		  
		    	  }
		    	
		    		if(count%12 == 0) {
		    			
		    			  xml_Y=xml_Y+"<Row>";
			                xml_T=xml_T+"<Row>";	                
			                value1 = Integer.toString(tag1);
			                
			                xml_Y=xml_Y+"<month>"+"12"+"</month>\\n";
			               
				    		xml_Y=xml_Y+"<rain>"+value1+"</rain>";
				    			
				    		value2 = Integer.toString(tag2);		
				    		 xml_T=xml_T+"<month>"+"12"+"</month>\\n";
				    		xml_T=xml_T+"<rain>"+value2+"</rain>";
				    	xml_Y=xml_Y+"</Row>"+"\n";
				    	xml_T=xml_T+"</Row>"+"\n";    
		    			
		    		}else {
		    			  xml_Y=xml_Y+"<Row>";
			                xml_T=xml_T+"<Row>";	                
			                value1 = Integer.toString(tag1);
			                
			                xml_Y=xml_Y+"<month>"+count%12+"</month>\\n";
			              
				    		xml_Y=xml_Y+"<rain>"+value1+"</rain>";
				    			
				    		value2 = Integer.toString(tag2);		
				    		 xml_T=xml_T+"<month>"+count%12+"</month>\\n";
				    		xml_T=xml_T+"<rain>"+value2+"</rain>";
				    	xml_Y=xml_Y+"</Row>"+"\n";
				    	xml_T=xml_T+"</Row>"+"\n";    
				    	
		    			
		    			
		    		}
		    		
			    	count=count+1;
			    
	    		
		    
			} 
		   
			
	    	writerY.write(xml_Y);
	    	writerT.write(xml_T);
		    missn= numTestData-MissClassificationRate_Testing;
		    //MissClassificationRate_Testing=missn;
		    double TestingAccuracy1;
		    TestingAccuracy1 = 1 - MissClassificationRate_Testing*1.0f/numTestData;
		    TestingAccuracy=100*TestingAccuracy1;
		    
		    System.out.println("MissClassificationRate_Testing : "+MissClassificationRate_Testing);
		    System.out.println("numTestData : "+numTestData);
		    
		    writerT.write("</Results>");
    		writerY.write("</Results>");
    		writerT.close();
    		writerY.close();
		   
//		   ff.getMatrixToWrite(xml_Y,"Y");
//		   ff.getMatrixToWrite(xml_T, "T");
		  
		     
//		    for (int i = 0; i < numTestData; i++) {
//		    	
//		    	System.out.println(resultR.get(i,0) + "\t\t" +resultM.get(i,1));
//		    }
		}
	}
	
	
	/*
	 * 
	 *
	 * -------------------------FOR RETURN VALUE ONLY!!!----------------------------------------
	 * 
	 * 
	 * */
	/*			BufferedWriter writer_test = new BufferedWriter(new FileWriter(new File("Output")));
	 String tt = String.valueOf(testT.get(0, i));
   String yy= String.valueOf(testY.get(0, i));
		writer_test.write(tt+" "+yy);
		
		writer_test.newLine();
	writer_test.flush();
	writer_test.close();
*/

	
	
	public double[] testOut(double[][] inpt){
		test_set = new DenseMatrix(inpt);
		return testOut();
	}
	public double[] testOut(double[] inpt){
		test_set = new DenseMatrix(new DenseVector(inpt));
		return testOut();
	}
	//Output	numTestData*NumberofOutputNeurons
	private double[] testOut(){
		numTestData = test_set.numRows();
		NumberofInputNeurons = test_set.numColumns()-1;
		
		DenseMatrix ttestT = new DenseMatrix(numTestData, 1);
		DenseMatrix ttestP = new DenseMatrix(numTestData, NumberofInputNeurons);
		for (int i = 0; i < numTestData; i++) {
			ttestT.set(i, 0, test_set.get(i, 0));
			for (int j = 1; j <= NumberofInputNeurons; j++)
				ttestP.set(i, j-1, test_set.get(i, j));
		}
		
		testT = new DenseMatrix(1,numTestData);
		testP = new DenseMatrix(NumberofInputNeurons,numTestData);
		ttestT.transpose(testT);
		ttestP.transpose(testP);
		//test_set.transpose(testP);
		
		DenseMatrix tempH_test = new DenseMatrix(NumberofHiddenNeurons, numTestData);
		InputWeight.mult(testP, tempH_test);
		DenseMatrix BiasMatrix2 = new DenseMatrix(NumberofHiddenNeurons, numTestData);
		
		for (int j = 0; j < numTestData; j++) {
			for (int i = 0; i < NumberofHiddenNeurons; i++) {
				BiasMatrix2.set(i, j, BiasofHiddenNeurons.get(i, 0));
			}
		}
	
		tempH_test.add(BiasMatrix2);
		DenseMatrix H_test = new DenseMatrix(NumberofHiddenNeurons, numTestData);
		
		if(func.startsWith("sig")){
			for (int j = 0; j < NumberofHiddenNeurons; j++) {
				for (int i = 0; i < numTestData; i++) {
					double temp = tempH_test.get(j, i);
					temp = 1.0f/ (1 + Math.exp(-temp));
					H_test.set(j, i, temp);
				}
			}
		}
		else if(func.startsWith("sin")){
			for (int j = 0; j < NumberofHiddenNeurons; j++) {
				for (int i = 0; i < numTestData; i++) {
					double temp = tempH_test.get(j, i);
					temp = Math.sin(temp);
					H_test.set(j, i, temp);
				}
			}
		}
		else if(func.startsWith("hardlim")){
			
		}
		else if(func.startsWith("tribas")){
	
		}
		else if(func.startsWith("radbas")){
			
		}
		
		DenseMatrix transH_test = new DenseMatrix(numTestData,NumberofHiddenNeurons);
		H_test.transpose(transH_test);
		DenseMatrix Yout = new DenseMatrix(numTestData,NumberofOutputNeurons);
		transH_test.mult(OutputWeight,Yout);
		
		//DenseMatrix testY = new DenseMatrix(NumberofOutputNeurons,numTestData);
		//Yout.transpose(testY);
		
		double[] result = new double[numTestData];
		
		if(Elm_Type == 0){
			for (int i = 0; i < numTestData; i++)
				result[i] = Yout.get(i, 0);
		}
		
		else if(Elm_Type == 1){
			for (int i = 0; i < numTestData; i++) {
				int tagmax = 0;
				double tagvalue = Yout.get(i, 0);
				for (int j = 1; j < NumberofOutputNeurons; j++)
				{
					if(Yout.get(i, j) > tagvalue){
						tagvalue = Yout.get(i, j);
						tagmax = j;
					}
		
				}
				result[i] = tagmax;
			}
		}
		return result;
	}
	
	public float getTrainingTime() {
		return TrainingTime;
	}
	public double getTrainingAccuracy() {
		return TrainingAccuracy;
	}
	public float getTestingTime() {
		return TestingTime;
	}
	public double getTestingAccuracy() {
		return TestingAccuracy;
	}
	
	public int getNumberofInputNeurons() {
		return NumberofInputNeurons;
	}
	public int getNumberofHiddenNeurons() {
		return NumberofHiddenNeurons;
	}
	public int getNumberofOutputNeurons() {
		return NumberofOutputNeurons;
	}
	
	public DenseMatrix getInputWeight() {
		return InputWeight;
	}
	
	public DenseMatrix getBiasofHiddenNeurons() {
		return BiasofHiddenNeurons;
	}
	
	public DenseMatrix getOutputWeight() {
		return OutputWeight;
	}

	//for predicting a data file based on a trained model.
	public void testgetoutput(String filename) throws IOException {
		
		try {
			test_set = loadmatrix(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		numTestData = test_set.numRows();
		NumberofInputNeurons = test_set.numColumns() - 1;
		
		
		double rsum = 0;
		double []actual = new double[numTestData];
		
		double [][]data = new double[numTestData][NumberofInputNeurons];
		for (int i = 0; i < numTestData; i++) {
			actual[i] = test_set.get(i, 0);
			for (int j = 0; j < NumberofInputNeurons; j++)
				data[i][j] = test_set.get(i, j+1);
		}
		
		double[] output = testOut(data);
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("Output")));
		for (int i = 0; i < numTestData; i++) {
			
			writer.write(String.valueOf(output[i]));
			writer.newLine();
			
			if(Elm_Type == 0){
					rsum += (output[i] - actual[i])*(output[i] - actual[i]);
			}
			
			if(Elm_Type == 1){
				if(output[i] == actual[i])
					rsum ++;
			}
			
		}
		writer.flush();
		writer.close();
		
		if(Elm_Type == 0)
			System.out.println("Regression GetOutPut RMSE: "+Math.sqrt(rsum*1.0f/numTestData));
		else if(Elm_Type == 1)
			System.out.println("Classfy GetOutPut Right: "+rsum*1.0f/numTestData);
	}
	
}
