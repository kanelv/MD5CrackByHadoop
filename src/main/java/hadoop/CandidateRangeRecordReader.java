package hadoop;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;

/**
 * @author Cahgnexx
 *
 */
public class CandidateRangeRecordReader extends RecordReader<Text, Text> {
    private String rangeBegin;
    private String rangeEnd;
    private boolean done = false;

    CandidateRangeRecordReader() {

    }

    @Override
    public Text getCurrentKey() throws IOException, InterruptedException {
        return new Text(rangeBegin);
    }

    @Override
    public Text getCurrentValue() throws IOException, InterruptedException {
        return new Text(rangeEnd);
    }

    // Với tham số truyền vào là một inputSplit, lớp này chia inputSplit thành 1 cặp key/value
    @Override
    public void initialize(InputSplit split, TaskAttemptContext context)
            throws IOException, InterruptedException {
        CandidateRangeInputSplit candidataRangeSplit = (CandidateRangeInputSplit) split;

        rangeBegin = candidataRangeSplit.getInputRange();
        rangeEnd = String.valueOf(Long.valueOf(rangeBegin) + candidataRangeSplit.getLength());

    }

    // Trong RecordReader được gọi lại nhiều để nạp các cặp key/value vào mapper,
    // khi gọi đến hết thì trả về false và task map hoàn thành, nhưng ở đây nó chỉ gọi 1 lần
    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {
        boolean tmpDone = false;
        if (done == false) {
            tmpDone = true;
            done = true;
        }

        return tmpDone;
    }

    // Trả về % dữ liệu đã đọc được
    @Override
    public float getProgress() throws IOException, InterruptedException {
        if (done) {
            return 1.0f;
        }
        return 0.0f;
    }

    @Override
    public void close() throws IOException {
    }
}
