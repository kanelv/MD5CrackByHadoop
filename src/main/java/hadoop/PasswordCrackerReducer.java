package hadoop;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @author Cahgnexx
 *
 */
//Lop Reducer Ghi ra password và encrypted password vào file
public class PasswordCrackerReducer extends Reducer<Text, Text, Text, Text> {
    public void reduce(Text encrypted, Text password, Context context) throws IOException, InterruptedException {
        context.write(encrypted, password);
    }
}
