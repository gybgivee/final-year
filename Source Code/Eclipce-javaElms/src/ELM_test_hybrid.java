
import java.io.IOException;

import no.uib.cipr.matrix.NotConvergedException;
public class ELM_test_hybrid {
	public static void main(String[] args) throws NotConvergedException, IOException {
	try {
		
		
		
			
			ELM_process_hybrid cls_hy = new ELM_process_hybrid(1, 300, "sig");
			
			
			cls_hy.train("src/16col_train.csv");
			cls_hy.test("src/16col_test.csv");
			
			System.out.println("---------------------------------- Accuracy -------------------------------------");
			System.out.println("TrainingTime : "+cls_hy.getTrainingTime());
			System.out.println("TrainingAcc : "+cls_hy.getTrainingAccuracy());
			System.out.println("TestingTime : "+cls_hy.getTestingTime());
			System.out.println("TestAcc : "+cls_hy.getTestingAccuracy());
            

			
		
			
			
			
			
		
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	   }

}}
