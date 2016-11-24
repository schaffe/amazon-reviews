package com.dzidzoiev.amazonreviews;

import akka.actor.UntypedActor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;

import static com.dzidzoiev.amazonreviews.CSVFileReader.EVENTS.FILE_PROCESSING_FINISHED;

public class CSVFileReader extends UntypedActor {

    enum EVENTS {
        FILE_PROCESSING_FINISHED
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof Kernel.StartProcessingMessage) {
            Kernel.StartProcessingMessage process = (Kernel.StartProcessingMessage) message;
            readFile(process.filename, process.itemsCount);
        }
        unhandled(message);
    }

    private void readFile(String file, int itemsCount) throws IOException, URISyntaxException {
        try (Reader in = new FileReader(file)) {
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
            int processed = 0;
            for (CSVRecord record : records) {
                if (processed++ == itemsCount)
                    break;
                ReviewItem reviewItem = parseOneItem(record);
                context().parent().tell(reviewItem, getSelf());
            }
        }
        context().parent().tell(FILE_PROCESSING_FINISHED, getSelf());
    }

    private ReviewItem parseOneItem(CSVRecord record) {
        return new ReviewItem(
                Integer.parseInt(record.get("Id")),
//                record.get("ProductId"),
                record.get("UserId"),
                record.get("ProfileName"),
//                Integer.parseInt(record.get("Score")),
//                Long.parseLong(record.get("Time")),
//                record.get("Summary"),
                record.get("Text")
        );
    }
}
