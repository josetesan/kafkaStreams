package es.ing.poc;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

public interface KStreamProcessor {

        @Input("input")
        KStream<?, ?> input();

        @Output("output")
        KStream<?, ?> output();
}
