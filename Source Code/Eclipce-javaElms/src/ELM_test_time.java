

import java.io.IOException;

import no.uib.cipr.matrix.NotConvergedException;
public class ELM_test_time {
	public static void main(String[] args) throws NotConvergedException, IOException {
	try {
		
		
		
			
			ELM_process_time cls_tt = new ELM_process_time(1, 300, "sig");
			
			
			cls_tt.train("src/12col_train.csv");
			cls_tt.test("src/12col_test.csv");
			
			//cls_tt.train("src/muang-train.csv");
			//cls_tt.test("src/muang-test.csv");
			
			//cls_tt.train("src/nongnakum_train.csv");
			//cls_tt.test("src/nongnakum_test.csv");
			
			System.out.println("---------------------------------- Accuracy -------------------------------------");
			System.out.println("TrainingTime : "+cls_tt.getTrainingTime());
			System.out.println("TrainingAcc : "+cls_tt.getTrainingAccuracy());
			System.out.println("TestingTime : "+cls_tt.getTestingTime());
			System.out.println("TestAcc : "+cls_tt.getTestingAccuracy());
            

			
		
			
			
			
			
		
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	   }

}}
