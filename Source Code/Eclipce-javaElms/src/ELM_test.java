import java.io.IOException;

import no.uib.cipr.matrix.NotConvergedException;
public class ELM_test {
	public static void main(String[] args) throws NotConvergedException, IOException {
	try {
		
		
			ELM_process cls_ELM = new ELM_process(1, 300, "sig");
			

			cls_ELM.train("src/4col_train.csv");
			cls_ELM.test("src/4col_test.csv");
			
			
			System.out.println("---------------------------------- Accuracy -------------------------------------");
			System.out.println("TrainingTime : "+cls_ELM.getTrainingTime());
			System.out.println("TrainingAcc : "+cls_ELM.getTrainingAccuracy());
			System.out.println("TestingTime : "+cls_ELM.getTestingTime());
			System.out.println("TestAcc : "+cls_ELM.getTestingAccuracy());
            
			
			

			
		
			
			
			
			
		
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	   }

}}
