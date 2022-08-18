Celebrate is a small script to print out different type of aniversaries.

```
CELEBRATE_FILE=events.csv go run ./main.go list
2022-08-31 Marriage 680 months
2022-10-12 Name 1 39 years
2022-11-18 Name 2 8500 days
2022-12-11 Name 2 280 months
2022-12-12 Name 1 470 months
2022-12-31 Marriage 57 years
2023-06-30 Marriage 21000 days
2023-08-11 Name 2 24 years
2023-10-12 Name 1 40 years
2023-12-11 Name 2 8888 days
2024-04-01 Name 2 9000 days
2024-05-01 Marriage 700 months
2024-08-11 Name 2 300 months
2024-11-05 Name 1 15000 days
2025-06-12 Name 1 500 months
2025-12-31 Marriage 60 years
2026-11-03 Marriage 22222 days
2029-08-11 Name 2 30 years
2038-07-15 Name 1 20000 days
2048-02-19 Marriage 30000 days
```

Can be used standalone as a waybar script:

```
  "custom/celebrate":{
    "return-type": "json",
    "interval": 3600,
    "exec":"~/go/bin/celebrate bar"
  },
```

