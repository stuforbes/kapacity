# Kapacity

## What is it?

A tool to enable the generation of data, and passing it to a system-under-test at a specified rate, all whilst having the
ability to record the performance of the system.

## How does it work?

When running the Kapacity engine, a few implementations must be defined, which ultimately control how data is generated, 
where the data is posted, and how results are collated. The interfaces that require implementations are:

1. com.stuforbes.kapacity.data.DataLoader

    Implementations of this interface load the data to be posted, returning a list of scenarios to be executed
    
2. com.stuforbes.kapacity.runner.data.DataPoster

    Describes how the data item should be posted to the system
    
3. com.stuforbes.kapacity.recorder.FlightRecorder

    Used to record the performance of the system under test. Returns the results when it is stopped
    
4. com.stuforbes.kapacity.result.ResultPrinter

    Prints the results of the test upon completion 


## Getting started

### Sample project

There is a sample application, under the folder sample/application. This application has 2 different methods to kapacity test:

1. An Http REST endpoint
2. A Kafka consumer and producer

There is a project to kapacity test this sample application, under the folder sample/kapacity-test.

#### How do I run it?

##### Application

You will need both Gradle and Docker installed locally to start up the application.
To start everything up, run the script run.sh from the application folder. This will:
 - Build the application jar file using Gradle
 - Build the Docker image for the application
 - Start up the application and it's dependent services using Docker Compose

##### Kapacity tests

From the sample/kapacity-test folder, run the command `gradle kapacity-test`. This will run the tests against the sample 
application for a duration of 20 seconds, and print the results to the console.

The test execution is configured in the build.gradle file as follows:


`
kapacity {
    duration = 20000
    dataLoader = "com.stuforbes.kapacity.sample.data.SampleDataLoader"
    timeSeriesDataSorter = "com.stuforbes.kapacity.data.StandardTimeSeriesDataSorter"
    dataPoster = "com.stuforbes.kapacity.sample.data.KafkaDataPosterImpl"
    flightRecorder = "com.stuforbes.kapacity.sample.recorder.PrometheusFlightRecorderImpl"
    resultFormatter = "com.stuforbes.kapacity.recorder.prometheus.PrometheusResultFormatter"
}
`

## Configuration

All configuration should be added to a file in the `src/main/resources` folder, named `test.properties`. These properties
can be accessed using extension functions in the class com.stuforbes.kapacity.configuration.ConfigKt. These extension functions are:

- intConfig()
- longConfig()
- stringConfig()
- booleanConfig()

Examples of how to use them can be found in `com.stuforbes.kapacity.sample.data.SampleDataLoader`


## Components

The versatility of Kapacity is in the various combinations of components that can be configured. The sections below describe
the different component interfaces, and where applicable, some implementations that are provided.

### com.stuforbes.kapacity.data.DataLoader

A Mechanism to load the data that will be used in the test run. Data is to be provided in a list of Scenarios (com.stuforbes.kapacity.model.Scenario).
Each scenario should be used to describe all interactions for a particular entity. For example, when kapacity testing a standard website,
a scenario could describe a single users interactions with the site.

Classes implementing the interface must override the function `loadData()` which takes no arguments and returns a list of scenarios.

### com.stuforbes.kapacity.runner.data.DataPoster

Implementations of this interface will post a single data item into the system-under-test.

This interface has a single function that requires implementing: `post(data: T)` where data is the object that will be
posted.

Kapacity includes 2 implementations of DataPoster out of the box:

1. com.stuforbes.kapacity.runner.data.HttpDataPoster

    Data items are serialised to JSON and sent via an HTTP POST request to the specified url.
    Response status codes and response times are recorded via the flight recorder, if the flight recorder also implements
    `DataPointRecordingFlightRecorder<Int, HttpResults>`. e.g. see `com.stuforbes.kapacity.recorder.http.HttpResponseRecorder`
    
2. com.stuforbes.kapacity.runner.data.KafkaDataPoster

    Data items are serialised to JSON and sent via the Kafka topic.


### com.stuforbes.kapacity.recorder.FlightRecorder

There are 2 functions that require implementing for this interface:

1. `start()` Start recording
2. `stop(): Result` Stop recording and return the results

Kapacity provides 3 implementations of FlightRecorder:

1. com.stuforbes.kapacity.recorder.NoOpFlightRecorder

    No recording takes place within Kapacity
    
2. com.stuforbes.kapacity.recorder.http.HttpResponseRecorder

    This recorder also extends the `com.stuforbes.kapacity.recorder.DataPointRecorder` interface, which records the
    response times of each request. When complete, the results are returned in a `com.stuforbes.kapacity.recorder.http.HttpResults` object.
    
3. com.stuforbes.kapacity.recorder.prometheus.PrometheusFlightRecorder

    Queries the desired latency metric from Prometheus for the duration between start() and stop() being invoked.
    The results are returned in a `com.stuforbes.kapacity.recorder.prometheus.PrometheusResult` object.
    
### com.stuforbes.kapacity.result.ResultPrinter

Prints the results to the required output stream. The ResultPrinter is configured with an implementation of 
`com.stuforbes.kapacity.result.ResultFormatter`, which describes how to render the result in a string format.

There are 2 implementations of ResultPrinter provided out of the box:

1. com.stuforbes.kapacity.result.ResultPrinterKt.consoleResultPrinter

    Renders the results to the console upon completion
    
2. com.stuforbes.kapacity.result.ResultPrinterKt.fileResultPrinter

    Renders the results to the specified file upon completion