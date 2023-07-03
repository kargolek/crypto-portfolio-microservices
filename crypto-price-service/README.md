# Crypto-price-service


### API Documentation
| Method | URI | Description | Request Body | Response Body |
|---|---|---|---|---|
| GET | `/api/v1/cryptocurrency/{id}` | Get a cryptocurrency by ID | None | CryptocurrencyDTO |
| GET | `/api/v1/cryptocurrency/contract-address/{contractAddress}` | Get a cryptocurrency by smart contract address | None | CryptocurrencyDTO |
| GET | `/api/v1/cryptocurrency` | Get all cryptocurrencies | Optional[List[String]] names | List[CryptocurrencyDTO] |
| POST | `/api/v1/cryptocurrency` | Register a new cryptocurrency | CryptocurrencyPostDTO | CryptocurrencyDTO |
| DELETE | `/api/v1/cryptocurrency/{id}` | Delete a cryptocurrency by ID | None | None |
| PUT | `/api/v1/cryptocurrency/{id}` | Update a cryptocurrency | CryptocurrencyPostDTO | CryptocurrencyDTO |