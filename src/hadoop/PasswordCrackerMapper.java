package hadoop;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * @author Cahgnexx
 *
 */
public class PasswordCrackerMapper extends Mapper<Text, Text, Text, Text>{
	
	// Sau khi doc cap key/value, tinh toan password su dung phuong thuc trong lop PasswordCrackerUtil
	// Neu nhan duoc mot password, chuyen password den reducer
	
	public void map(Text key, Text value, Context context) throws IOException, InterruptedException{
		Configuration 	conf 			= context.getConfiguration();
		String 			flagFilename 	= conf.get("terminationFlagFilename");
		FileSystem 		hdfs 			= FileSystem.get(conf);
		
		TerminationChecker terminationChecker = new TerminationChecker(hdfs, flagFilename);
		
		long rangeBegin = Long.valueOf(key.toString());
		long rangeEnd 	= Long.valueOf(value.toString());
		
		String encryptedPassword = conf.get("encryptedPassword");
		String password = PasswordCrackerUtil.findPasswordInRange(rangeBegin, rangeEnd, encryptedPassword, terminationChecker);
		if(password != null){
			context.write(new Text(encryptedPassword), new Text(password));
		}			
	}
}

	// Lop dung de kiem tra khi nao thi dung 
	// Neu mot task tim thay password, task tao 1 file su dung phuong thuc trong lop nay, 
	// do do task se xac dinh khi nao ket thuc hay khong bang cach kiem tra su hien dien cua file

class TerminationChecker{
	FileSystem fs;
	Path flagPath;
	boolean isDone = false;
	
	TerminationChecker(FileSystem fs, String flagFilename){
		this.fs = fs;
		this.flagPath = new Path(flagFilename);
	}
	
	public boolean isTerminated() throws IOException{
		return isDone;
	}
	
	public void setTerminated() throws IOException{
		fs.create(flagPath);
	}
	
	public void checkPeriodically(){
		Runnable temp = new Runnable(){

			@Override
			public void run() {
				try{
					Thread.sleep(1000);
					if(fs.exists(flagPath))
						isDone = true;
				} catch(Exception e){
					
				}
				
			}};
			new Thread(temp).start();		
	}
}