bash -c 'echo Waiting for Kafka to be ready... &&
         cub kafka-ready -b broker:9092 1 60 &&
         kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1 --topic wb-list-command  &&
         kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1 --topic wb-list-event-sink &&
         kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1 --topic result  &&
         kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1 --topic p2p_result  &&
         kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1 --topic fraud_payment &&
         kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1 --topic payment_event &&
         kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1 --topic refund_event &&
         kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1 --topic chargeback_event &&
         kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic template &&
         kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --topic full_template &&
         kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic template_p2p &&
         kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic template_reference &&
         kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --topic full_template_reference &&
         kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic template_p2p_reference &&
         kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic group_list &&
         kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --topic full_group_list &&
         kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic group_p2p_list &&
         kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic group_reference &&
         kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --topic full_group_reference &&
         kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1  --config cleanup.policy=compact --topic group_p2p_reference &&
         echo Waiting 60 seconds for Connect to be ready... &&
         sleep 60'