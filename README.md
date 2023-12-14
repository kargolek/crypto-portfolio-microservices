# Crypto-Portfolio [![Trello](https://img.shields.io/badge/Trello-0052CC?style=for-the-badge&logo=trello&logoColor=white)](https://trello.com/b/PnS0tEYn/crypto-portfolio-app)
[![codecov](https://codecov.io/github/kargolek/crypto-portfolio-microservices/branch/main/graph/badge.svg?token=EF3DVKJ11I)](https://codecov.io/github/kargolek/crypto-portfolio-microservices)
[![Build Status](https://github.com/kargolek/crypto-portfolio-microservices/workflows/build-and-run-unit-integration-test/badge.svg)](https://github.com/kargolek/crypto-portfolio-microservices/actions/workflows/build.yaml)
[![Full Test](https://github.com/kargolek/crypto-portfolio-microservices/workflows/qa-main-full-test/badge.svg)](https://kargolek.github.io/crypto-portfolio-microservices/main/) 
[![Regression Test](https://github.com/kargolek/crypto-portfolio-microservices/workflows/qa-regression-test-chrome/badge.svg)](https://kargolek.github.io/crypto-portfolio-microservices/regression/chrome)
[![Regression Test](https://github.com/kargolek/crypto-portfolio-microservices/workflows/qa-regression-test-edge/badge.svg)](https://kargolek.github.io/crypto-portfolio-microservices/regression/edge)
[![Regression Test](https://github.com/kargolek/crypto-portfolio-microservices/workflows/qa-regression-test-firefox/badge.svg)](https://kargolek.github.io/crypto-portfolio-microservices/regression/firefox)

Crypto-Portfolio is an application that allows users to check the balances of their cryptocurrency portfolios for various tokens such as Ethereum, Matic, and Avalanche. 
It provides a convenient way to monitor multiple wallet addresses simultaneously.

## Table of content
- [Features](#features)
- [How to use](#how-to-use)
- [License](#license)
- [Examples](#examples)
- [API documentation](#api-documentation)
- [Quality Assurance](#api-documentation)

## Features

- **Multi-Token Support**: The application checks the balances of crypto wallets for different tokens, including Ethereum, Matic, and Avalanche. More tokens will be added in the future.
- **Multi-Wallet Check**: Users can check the balances of up to 70 wallet addresses at once.
- **Balance Summation**: The application calculates the total balances across all wallets and presents them to the user in both numerical and graphical (treemap chart) formats.
- **Historical Balance**: The application provides the wallet balances for past durations such as 1 hour, 24 hours, 7 days, 30 days, 60 days, and 90 days, based on the historical prices and current wallet balances.
- **Current Token Prices**: Users can access real-time prices of the most popular cryptocurrencies obtained from CoinmarketCap.
- **Frontend**: Angular 15.1
- **Backend**: Spring Boot 2.7 (Microservices Architecture)
- **Database**: MySQL
- **Unit Testing, Integration Testing, and E2E Testing**: The application provides comprehensive test coverage to ensure its stability and reliability.
- **Cross-Platform**: The application is built to support both desktop and mobile browsers.

## How to Use

*Preconditions*
1. Create an account on the https://coinmarketcap.com/ and add API key "COIN_MARKET_CAP_API_KEY" to system env variable
2. Create an account on the https://etherscan.io/ and add API key "ETHERSCAN_API_KEY" to system env variable
3. Create an account on the https://snowtrace.io/ and add API key "AVALANCHE_API_KEY" to system env variable
4. Create an account on the https://polygonscan.com/ and add API key "POLYGON_API_KEY" to system env variable
5. Make sure you have Docker installed on your environment

*Main Steps*
1. Clone repo
2. Run cmd script for build and compose docker containers
```shell
start build_env.bat
```
3. Navigate to
* http://localhost:4201
* http://<host_machine_ip>:4201

The app will start with preloaded data for Bitcoin, Ethereum, Matic, Avax tokens.

## License

This project is licensed under the MIT LICENSE. See the [LICENSE](LICENSE) file for more information.

## Examples

#### Desktop version

* Input wallet view
![web_screen1](https://github.com/kargolek/crypto-portfolio-microservices/assets/33175703/d8ea8ef2-1d57-4edf-abb3-91c96b276d41)


* Portfolio view
![web_screen2](https://github.com/kargolek/crypto-portfolio-microservices/assets/33175703/715ab29f-a5d0-4a04-be63-dba4e3a4b54c)
![web_screen3](https://github.com/kargolek/crypto-portfolio-microservices/assets/33175703/d2464acc-4c22-4ecf-99de-5b8354c50044)
![web_screen4](https://github.com/kargolek/crypto-portfolio-microservices/assets/33175703/35c5b72d-c74c-4ed7-a800-8e6446aab0b8)

#### Mobile version
https://github.com/kargolek/crypto-portfolio-microservices/assets/33175703/1066fb1a-5582-45ed-90bc-f278ecef484d

## API Documentation
1. [crypto-price service](https://github.com/kargolek/crypto-portfolio-microservices/tree/main/crypto-price-service/README.md)
2. [wallet-service](https://github.com/kargolek/crypto-portfolio-microservices/tree/main/wallet-service/README.md)

## Quality Assurance
1. [**Unit & Integration tests** for **crypto-price-service**](https://github.com/kargolek/crypto-portfolio-microservices/tree/main/crypto-price-service/src/test/java/pl/kargolek/cryptopriceservice)
2. [**Unit & Integration tests** for **wallet-service**](https://github.com/kargolek/crypto-portfolio-microservices/tree/main/crypto-price-service/src/test/java/pl/kargolek/cryptopriceservice)
3. [**QA tests** for **API** and **UI**](https://github.com/kargolek/crypto-portfolio-microservices/tree/main/quality-assurance)