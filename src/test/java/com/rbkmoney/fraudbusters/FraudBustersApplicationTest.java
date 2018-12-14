package com.rbkmoney.fraudbusters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.junit.*;
import org.testcontainers.containers.ClickHouseContainer;
import ru.yandex.clickhouse.ClickHouseConnection;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

@Slf4j
public class FraudBustersApplicationTest {

    public static final String DEFAULT = "default";
    private ClickHouseDataSource dataSource;
    private ClickHouseConnection connection;

    @Rule
    public ClickHouseContainer clickhouse = new ClickHouseContainer();

    @Test
    public void testUpdateCountForSelect() throws Exception {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(clickhouse.getJdbcUrl());
        hikariConfig.setUsername(clickhouse.getUsername());
        hikariConfig.setPassword(clickhouse.getPassword());
        HikariDataSource ds = new HikariDataSource(hikariConfig);
        Statement statement = ds.getConnection().createStatement();
        statement.executeUpdate("CREATE TABLE `ontime` (\n" +
                "  `Year` UInt16,\n" +
                "  `Quarter` UInt8,\n" +
                "  `Month` UInt8,\n" +
                "  `DayofMonth` UInt8,\n" +
                "  `DayOfWeek` UInt8,\n" +
                "  `FlightDate` Date,\n" +
                "  `UniqueCarrier` FixedString(7),\n" +
                "  `AirlineID` Int32,\n" +
                "  `Carrier` FixedString(2),\n" +
                "  `TailNum` String,\n" +
                "  `FlightNum` String,\n" +
                "  `OriginAirportID` Int32,\n" +
                "  `OriginAirportSeqID` Int32,\n" +
                "  `OriginCityMarketID` Int32,\n" +
                "  `Origin` FixedString(5),\n" +
                "  `OriginCityName` String,\n" +
                "  `OriginState` FixedString(2),\n" +
                "  `OriginStateFips` String,\n" +
                "  `OriginStateName` String,\n" +
                "  `OriginWac` Int32,\n" +
                "  `DestAirportID` Int32,\n" +
                "  `DestAirportSeqID` Int32,\n" +
                "  `DestCityMarketID` Int32,\n" +
                "  `Dest` FixedString(5),\n" +
                "  `DestCityName` String,\n" +
                "  `DestState` FixedString(2),\n" +
                "  `DestStateFips` String,\n" +
                "  `DestStateName` String,\n" +
                "  `DestWac` Int32,\n" +
                "  `CRSDepTime` Int32,\n" +
                "  `DepTime` Int32,\n" +
                "  `DepDelay` Int32,\n" +
                "  `DepDelayMinutes` Int32,\n" +
                "  `DepDel15` Int32,\n" +
                "  `DepartureDelayGroups` String,\n" +
                "  `DepTimeBlk` String,\n" +
                "  `TaxiOut` Int32,\n" +
                "  `WheelsOff` Int32,\n" +
                "  `WheelsOn` Int32,\n" +
                "  `TaxiIn` Int32,\n" +
                "  `CRSArrTime` Int32,\n" +
                "  `ArrTime` Int32,\n" +
                "  `ArrDelay` Int32,\n" +
                "  `ArrDelayMinutes` Int32,\n" +
                "  `ArrDel15` Int32,\n" +
                "  `ArrivalDelayGroups` Int32,\n" +
                "  `ArrTimeBlk` String,\n" +
                "  `Cancelled` UInt8,\n" +
                "  `CancellationCode` FixedString(1),\n" +
                "  `Diverted` UInt8,\n" +
                "  `CRSElapsedTime` Int32,\n" +
                "  `ActualElapsedTime` Int32,\n" +
                "  `AirTime` Int32,\n" +
                "  `Flights` Int32,\n" +
                "  `Distance` Int32,\n" +
                "  `DistanceGroup` UInt8,\n" +
                "  `CarrierDelay` Int32,\n" +
                "  `WeatherDelay` Int32,\n" +
                "  `NASDelay` Int32,\n" +
                "  `SecurityDelay` Int32,\n" +
                "  `LateAircraftDelay` Int32,\n" +
                "  `FirstDepTime` String,\n" +
                "  `TotalAddGTime` String,\n" +
                "  `LongestAddGTime` String,\n" +
                "  `DivAirportLandings` String,\n" +
                "  `DivReachedDest` String,\n" +
                "  `DivActualElapsedTime` String,\n" +
                "  `DivArrDelay` String,\n" +
                "  `DivDistance` String,\n" +
                "  `Div1Airport` String,\n" +
                "  `Div1AirportID` Int32,\n" +
                "  `Div1AirportSeqID` Int32,\n" +
                "  `Div1WheelsOn` String,\n" +
                "  `Div1TotalGTime` String,\n" +
                "  `Div1LongestGTime` String,\n" +
                "  `Div1WheelsOff` String,\n" +
                "  `Div1TailNum` String,\n" +
                "  `Div2Airport` String,\n" +
                "  `Div2AirportID` Int32,\n" +
                "  `Div2AirportSeqID` Int32,\n" +
                "  `Div2WheelsOn` String,\n" +
                "  `Div2TotalGTime` String,\n" +
                "  `Div2LongestGTime` String,\n" +
                "  `Div2WheelsOff` String,\n" +
                "  `Div2TailNum` String,\n" +
                "  `Div3Airport` String,\n" +
                "  `Div3AirportID` Int32,\n" +
                "  `Div3AirportSeqID` Int32,\n" +
                "  `Div3WheelsOn` String,\n" +
                "  `Div3TotalGTime` String,\n" +
                "  `Div3LongestGTime` String,\n" +
                "  `Div3WheelsOff` String,\n" +
                "  `Div3TailNum` String,\n" +
                "  `Div4Airport` String,\n" +
                "  `Div4AirportID` Int32,\n" +
                "  `Div4AirportSeqID` Int32,\n" +
                "  `Div4WheelsOn` String,\n" +
                "  `Div4TotalGTime` String,\n" +
                "  `Div4LongestGTime` String,\n" +
                "  `Div4WheelsOff` String,\n" +
                "  `Div4TailNum` String,\n" +
                "  `Div5Airport` String,\n" +
                "  `Div5AirportID` Int32,\n" +
                "  `Div5AirportSeqID` Int32,\n" +
                "  `Div5WheelsOn` String,\n" +
                "  `Div5TotalGTime` String,\n" +
                "  `Div5LongestGTime` String,\n" +
                "  `Div5WheelsOff` String,\n" +
                "  `Div5TailNum` String\n" +
                ") ENGINE = MergeTree(FlightDate, (Year, FlightDate), 8192)");

        PreparedStatement prepareStatement = ds.getConnection().prepareStatement("INSERT INTO ontime [(Year, Month , FlightDate)] VALUES (?, ?, ?);");
        prepareStatement.setInt(1, 1);
        prepareStatement.setInt(2, 1);
        prepareStatement.setDate(3, new java.sql.Date(1));
        boolean execute = prepareStatement.execute();

        statement = ds.getConnection().createStatement();
        statement.execute("select avg(c1) from (select Year, Month, count(*) as c1 from ontime group by Year, Month);");
        ResultSet resultSet = statement.getResultSet();
        resultSet.next();
        Date resultSetInt = resultSet.getDate(1);
        assertEquals("A basic SELECT query succeeds", 1, resultSetInt);
    }


}