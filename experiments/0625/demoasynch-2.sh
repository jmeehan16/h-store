#!/bin/bash
ant clean-all build-all

ant hstore-prepare -Dproject="voterdemohstoreasynchfile" -Dhosts="localhost:0:0"

python ./tools/autorunexp-3.py -p "voterdemohstoreasynchfile" -o "experiments/0625/voterdemohstoreasynchfile-1c-95-0625-site03.txt" \
--txnthreshold 0.95 -e "experiments/0625/site03-0625-voterdemohstoreasynch-2.txt" --winconfig "(site03) ramp async log h-store" \
--threads 1 --rmin 5000 --rmax 10100 --rstep 500 --finalrstep 500 --warmup 10000 --hstore --hscheduler --numruns 1 --recordramp --log

python ./tools/autorunexp-3.py -p "voterdemohstoreasynchfile" -o "experiments/0625/voterdemohstoreasynchfile-1c-95-0625-site03.txt" \
--txnthreshold 0.95 -e "experiments/0625/site03-0625-voterdemohstoreasynch-2.txt" --winconfig "(site03) ramp async no log h-store" \
--threads 1 --rmin 5000 --rmax 10100 --rstep 500 --finalrstep 500 --warmup 10000 --hstore --hscheduler --numruns 1 --recordramp
