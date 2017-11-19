package hadoop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

/**
 * @author Cahgnexx
 *
 */
public class CandidateRangeInputFormat extends InputFormat<Text, Text> {
    private List<InputSplit> splits;

    @Override
    public RecordReader<Text, Text> createRecordReader(InputSplit split, TaskAttemptContext context)
            throws IOException, InterruptedException {
        return new CandidateRangeRecordReader();
    }

    // Sinh ra cac khoang duyet vet can va tra ve cho JobClient
    @Override
    public List<InputSplit> getSplits(JobContext job) throws IOException, InterruptedException {
       splits = new ArrayList<>();

        int numberOfSplit = job.getConfiguration().getInt("numberOfSplit", 1);    //lay so khoang chia
        long subRangeSize = (PasswordCrackerUtil.TOTAL_PASSWORD_RANGE_SIZE + numberOfSplit - 1) / numberOfSplit;
 
        // Moi khoang con luu tru trong danh sach InputSlip
        for (int i = 0; i < numberOfSplit; i++) {
            long currentSubRange = i*subRangeSize;
            CandidateRangeInputSplit split = new CandidateRangeInputSplit(
                    String.valueOf(currentSubRange), subRangeSize, null);
            splits.add(split);
        }

        return splits;
    }
}
