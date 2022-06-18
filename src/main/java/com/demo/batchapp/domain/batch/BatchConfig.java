package com.demo.batchapp.domain.batch;

import java.text.Normalizer;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import com.demo.batchapp.domain.batch.chunk.Person;
import com.demo.batchapp.domain.batch.chunk.PersonRepository;
import com.demo.batchapp.domain.batch.tasklet.Task1;
import com.demo.batchapp.domain.batch.tasklet.Task2;
import lombok.extern.slf4j.Slf4j;

@EnableBatchProcessing // バッチの基本機能提供
@Configuration // Beanの生成(1つ以上のBeanが必要)
@Slf4j
public class BatchConfig {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private PersonRepository personRepository;

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory) {
        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .next(step2())
                .next(step3())
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("tasklet1")
                .tasklet(new Task1())
                .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("tasklet2")
                .tasklet(new Task2())
                .build();
    }

    @Bean
    public Step step3() {
        return stepBuilderFactory.get("chunk")
                .<Person, Person>chunk(10)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    /**
     * data.csv ファイルを読み込む
     * 
     * @return バッチ処理用 Reader item
     */
    @Bean
    public FlatFileItemReader<Person> itemReader() {
        return new FlatFileItemReaderBuilder<Person>().name("itemReader")
                .resource(new FileSystemResource("src/main/resources/data.csv"))
                .delimited()
                .names("firstName", "lastName")
                .targetType(Person.class)
                .build();
    }

    /**
     * Reader から渡されたオブジェクトを保存する形に変換する
     * 今回はアルファベットの大文字変換と、半角カナを全角カナに変換している
     *
     * @return 変換後に保存するアイテム
     */
    @Bean
    public ItemProcessor<Person, Person> itemProcessor() {
        return person -> {
            final String firstName = Normalizer.normalize(person.getFirstName().toUpperCase(),
                    Normalizer.Form.NFKC);
            final String lastName = Normalizer.normalize(person.getLastName().toUpperCase(),
                    Normalizer.Form.NFKC);

            final Person transformedPerson = new Person(firstName, lastName);

            log.info("before : " + person);
            log.info("after  : " + transformedPerson);

            return transformedPerson;
        };
    }

    /**
     * Processor から渡されたオブジェクトを書き込む（保存処理をかける）
     * 
     * @return 保存処理用のリポジトリ
     */
    @Bean
    public RepositoryItemWriter<Person> itemWriter() {
        return new RepositoryItemWriterBuilder<Person>().repository(personRepository).methodName("save").build();
    }
}
