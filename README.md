# Fraudbusters 

Service for fraud detection.

It is a tool for describing behavioral patterns for fraudulent transactions, it provides the ability 
to check for compliance with the described patterns.

### Template Description Language

[Fraudo DSL](https://github.com/rbkmoney/fraudo)

### The following protocols are implemented for interaction
[Apache Thrift](https://thrift.apache.org/) is used to describe the protocols.

- [Proxy Inspector](https://github.com/rbkmoney/damsel/blob/master/proto/proxy_inspector.thrift) - 
protocol for inspection payment for mismatch
- [Fraudbusters Proto](https://github.com/rbkmoney/fraudbusters-proto/blob/master/proto/fraudbusters.thrift) -
protocol for managing a set of patterns and bindings

### Installation and testing

Instruction for testing in https://github.com/rbkmoney/fraudbusters-compose

### License
[Apache 2.0 License.](/LICENSE)

