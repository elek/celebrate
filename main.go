package main

import (
	"encoding/json"
	"fmt"
	"github.com/spf13/cobra"
	"github.com/zeebo/errs/v2"
	"log"
	"math"
	"os"
	"path"
	"sort"
	"strconv"
	"strings"
	"time"
)

func main() {
	g := cobra.Command{
		Use: "celebrate",
	}

	g.AddCommand(&cobra.Command{
		Use: "list",
		RunE: func(cmd *cobra.Command, args []string) error {
			return run()
		},
	})
	g.AddCommand(&cobra.Command{
		Use: "bar",
		RunE: func(cmd *cobra.Command, args []string) error {
			return bar()
		},
	})
	err := g.Execute()
	if err != nil {
		log.Fatalf("%++v", err)
	}
}

type Event struct {
	Name string
	Date time.Time
}

type Anniversary struct {
	Event  Event
	Date   time.Time
	Score  int
	Amount int
	Unit   string
}

type timeDiff func(time.Time, time.Time) int
type timeAdd func(time.Time, int) time.Time

func bar() error {

	anniversaries, err := calculateAnniversaries()
	if err != nil {
		return err
	}
	if len(anniversaries) > 4 {
		anniversaries = anniversaries[0:4]
	}
	t := ""

	for _, a := range anniversaries {
		if t != "" {
			t += "\n"
		}
		t += fmt.Sprintf("%s %s %d %s", a.Date.Format("2006-01-02"), a.Event.Name, a.Amount, a.Unit)
	}

	remaining := int(anniversaries[0].Date.Sub(time.Now()).Hours() / 24)
	res := struct {
		Text    string `json:"text"`
		Tooltip string `json:"tooltip"`
	}{
		Text:    fmt.Sprintf("🎂 %d", remaining),
		Tooltip: t,
	}

	out, err := json.Marshal(res)
	if err != nil {
		return errs.Wrap(err)
	}
	fmt.Println(string(out))
	return nil
}

func run() error {
	anniversaries, err := calculateAnniversaries()
	if err != nil {
		return err
	}
	for _, a := range anniversaries {
		if a.Score > 0 {
			fmt.Printf("%s %s %d %s\n", a.Date.Format("2006-01-02"), a.Event.Name, a.Amount, a.Unit)
		}
	}
	fmt.Println()

	return nil
}

func calculateAnniversaries() ([]Anniversary, error) {
	configFile := os.Getenv("CELEBRATE_FILE")
	if configFile == "" {
		home, err := os.UserHomeDir()
		if err != nil {
			return nil, errs.Wrap(err)
		}

		configFile = path.Join(home, ".config", "celebrate", "events.csv")
	}
	events, err := readEvents(configFile)
	if err != nil {
		return nil, err
	}
	var anniversaries []Anniversary
	for _, e := range events {
		anniversaries = append(anniversaries, generateAnniversary(e)...)
	}
	for _, a := range anniversaries {
		if a.Score == 0 {
			fmt.Printf("%s %s %d %s\n", a.Date.Format("2006-01-02"), a.Event.Name, a.Amount, a.Unit)
		}
	}
	sort.Slice(anniversaries, func(i, j int) bool {
		return anniversaries[i].Date.Before(anniversaries[j].Date)
	})
	return anniversaries, nil
}

func generateAnniversary(event Event) (res []Anniversary) {
	today := time.Now().Truncate(time.Hour * 24)
	res = append(res, createAnniversaries(today, event, dayDiff, dayAdd, "days")...)
	res = append(res, createAnniversaries(today, event, monthDiff, monthAdd, "months")...)
	res = append(res, createAnniversaries(today, event, yearDiff, yearAdd, "years")...)

	return res
}

func createAnniversaries(today time.Time, event Event, diff timeDiff, add timeAdd, unitName string) (res []Anniversary) {
	d := diff(event.Date, today)
	score := 6
	for l := 10000; l > 0; l /= 10 {
		if d > l {
			next := (d/l + 1) * l
			// we may already added it
			if next%(l*10) != 0 {
				res = append(res, createAnniversary(event, add, next, unitName, score))
			}
			score--
			if score < 5 {
				break
			}
		}
	}

	for _, i := range magicNumbers(d) {
		res = append(res, createAnniversary(event, add, i, unitName, 6))
	}
	return res
}

func magicNumbers(d int) (res []int) {

	formatted := fmt.Sprintf("%d", d)
	n := ""
	for i := 0; i < len(formatted); i++ {
		n += formatted[0:1]
	}
	v, _ := strconv.Atoi(n)
	if v > d {
		res = append(res, v)
	}
	return res
}

func createAnniversary(event Event, add timeAdd, next int, unitName string, score int) Anniversary {
	return Anniversary{
		Event:  event,
		Date:   add(event.Date, next),
		Score:  score,
		Amount: next,
		Unit:   unitName,
	}
}

func dayAdd(a time.Time, b int) time.Time {
	return a.Add(time.Hour * time.Duration(b) * 24)
}

func monthAdd(a time.Time, b int) time.Time {
	return a.AddDate(b/12, b%12, 0)
}

func yearAdd(a time.Time, b int) time.Time {
	return a.AddDate(b, 0, 0)
}

func dayDiff(a time.Time, b time.Time) int {
	diff := b.Sub(a)
	days := int(math.Floor(diff.Hours() / 24))
	return days
}

func monthDiff(a time.Time, b time.Time) int {
	y := b.Year() - a.Year()
	y *= 12
	if b.Day() < a.Day() {
		y--
	}
	return y + int(b.Month()) - int(a.Month())
}

func yearDiff(a time.Time, b time.Time) int {
	y := b.Year() - a.Year()
	if b.Month() < a.Month() {
		y -= 1
	}
	return y
}

func readEvents(file string) ([]Event, error) {
	var res []Event
	c, err := os.ReadFile(file)
	if err != nil {
		return nil, errs.Wrap(err)
	}
	for _, line := range strings.Split(string(c), "\n") {
		line = strings.TrimSpace(line)
		if line == "" {
			continue
		}
		parts := strings.SplitN(line, ";", 2)
		parsedDate, err := time.Parse("2006-01-02", parts[1])
		if err != nil {
			return nil, errs.Wrap(err)
		}
		res = append(res, Event{
			Name: parts[0],
			Date: parsedDate,
		})

	}
	return res, nil
}
