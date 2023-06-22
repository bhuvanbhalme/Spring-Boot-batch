package com.cglia.batch.dao;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.cglia.model.Student;
import com.cglia.repository.StudentRepository;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private StudentRepository studentRepository;

	@Bean
	public FlatFileItemReader<Student> reader() {
		FlatFileItemReader<Student> itemReader = new FlatFileItemReader<>();
		itemReader.setResource(new FileSystemResource("src/main/resources/loadData.csv"));
		itemReader.setName("csvReader");
		itemReader.setLinesToSkip(1);
		itemReader.setLineMapper(lineMapper());
		return itemReader;
	}

	private LineMapper<Student> lineMapper() {
		DefaultLineMapper<Student> lineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("id", "name", "email", "age", "address");

		BeanWrapperFieldSetMapper<Student> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Student.class);

		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		return lineMapper;
	}

	@Bean
	public StudentProcessor processor() {
		return new StudentProcessor();
	}

	@Bean
	public RepositoryItemWriter<Student> writer() {
		RepositoryItemWriter<Student> writer = new RepositoryItemWriter<>();
		writer.setRepository(studentRepository);
		writer.setMethodName("save");
		return writer;
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("csv-step").<Student, Student>chunk(10).reader(reader()).processor(processor())
				.writer(writer()).build();
	}

	@Bean
	public Job importStudentsJob() {
		return jobBuilderFactory.get("importStudentsJob").flow(step1()).end().build();
	}

	@Bean
	public FlatFileItemWriter<Student> getWriter() {
		return new FlatFileItemWriterBuilder<Student>().name("csvWriter")
				.resource(new FileSystemResource("src/main/resources/getData.csv"))
				.lineAggregator(new DelimitedLineAggregator<Student>() {
					{
						setDelimiter(",");
						setFieldExtractor(new BeanWrapperFieldExtractor<Student>() {
							{
								setNames(new String[] { "id", "name", "email", "age", "address" });
							}
						});
					}
				}).build();
	}

	@Bean
	public Step exportStep(FlatFileItemWriter<Student> writer) {
		return stepBuilderFactory.get("exportStep").<Student, Student>chunk(10).reader(reader()).writer(writer).build();
	}

	@Bean
	public Job exportStudentsJob(Step exportStep) {
		return jobBuilderFactory.get("exportStudentsJob").start(exportStep).build();
	}

}
