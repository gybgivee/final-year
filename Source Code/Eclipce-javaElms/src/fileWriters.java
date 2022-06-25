import java.io.*;
import no.uib.cipr.*;
import no.uib.cipr.matrix.DenseMatrix;
public class fileWriters {
 public static int name=1;

public String buildXml(int tag2,String oldXml) {
	String xml = oldXml;
	String value="";
	
	value = Integer.toString(tag2);				
	xml=xml+"<Row>";						
	xml=xml+"<rain>"+value+"</rain>";
	xml=xml+"</Row>"+"\n";
	
	return xml;
	
}    
public void getMatrixToWrite(String xml,String type) {
		// TODO Auto-generated method stub
		
		
	
		String pname=Integer.toString(name);
		
		
		
		String path = "src/"+type+pname+".txt";
		
		File file = new File(path);	
		FileWriter writer;
		
		try {
			
		writer = new FileWriter(file, true);  //True = Append to file, false = Overwrite

		xml=xml+"</Results>";
		
		
		
		writer.write(xml);
	
		writer.close();
		name++;
		 
		
		System.out.println("Write success!");
		
		 
		
		} catch (IOException e) {
		
		// TODO Auto-generated catch block
	
		e.printStackTrace();
	
		}

	}
}
