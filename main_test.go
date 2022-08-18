package main

import (
	"fmt"
	"github.com/stretchr/testify/require"
	"testing"
	"time"
)

func TestMonthDiff(t *testing.T) {
	require.Equal(t, 22, monthDiff(tp("2021-01-02"), tp("2022-11-29")))
	require.Equal(t, 11, monthDiff(tp("2021-12-02"), tp("2022-11-29")))
	require.Equal(t, 1, monthDiff(tp("2022-09-29"), tp("2022-11-28")))
}

func TestMonthAdd(t *testing.T) {
	require.Equal(t, tp("2022-12-24"), monthAdd(tp("2022-10-24"), 2))
	require.Equal(t, tp("2022-05-01"), monthAdd(tp("2022-03-31"), 1))
}

func TestDay(t *testing.T) {
	today := time.Now().UTC().Truncate(time.Hour * 24)
	fmt.Println(today)
	start := tp("2022-08-01")
	fmt.Println(start)
	diff := dayDiff(start, today)
	fmt.Println(diff)
	n := dayAdd(start, diff)
	fmt.Println(n)
	require.Equal(t, today, n)
}

func tp(s string) time.Time {
	a, _ := time.Parse("2006-01-02", s)
	return a
}
