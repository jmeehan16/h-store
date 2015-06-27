#!/bin/bash
ant clean-all build-all

ant hstore-prepare -Dproject="voterdemohstoreasynchfile" -Dhosts="localhost:0:0"
ant hstore-prepare -Dproject="voterdemohstorefile" -Dhosts="localhost:0:0"

python ./tools/autorunexp-3.py -p "voterdemohstoreasynchfile" -o "experiments/0625/voterdemohstoreasynchfile-1c-95-0625-site03.txt" \
--txnthreshold 0.95 -e "experiments/0625/site03-0625-voterdemohstoreasynch-1.txt" --winconfig "(site03) ramp async log h-store" \
--threads 1 --rmin 10000 --rmax 20000 --rstep 1000 --finalrstep 1000 --warmup 10000 --hstore --hscheduler --numruns 1 --duration 40000 --recordramp --log

python ./tools/autorunexp-3.py -p "voterdemohstorefile" -o "experiments/0625/voterdemohstorefile-1c-95-0625-site03.txt" \
--txnthreshold 0.95 -e "experiments/0625/site03-0625-voterdemohstore-1.txt" --winconfig "(site03) ramp log h-store" \
--threads 1 --rmin 100 --rmax 700 --rstep 50 --finalrstep 50 --warmup 10000 --hstore --hscheduler --numruns 1 --log --recordramp

python ./tools/autorunexp-3.py -p "voterdemohstoreasynchfile" -o "experiments/0625/voterdemohstoreasynchfile-1c-95-0625-site03.txt" \
--txnthreshold 0.95 -e "experiments/0625/site03-0625-voterdemohstoreasynch-1.txt" --winconfig "(site03) ramp async no log h-store" \
--threads 1 --rmin 10000 --rmax 20000 --rstep 1000 --finalrstep 1000 --warmup 10000 --hstore --hscheduler --numruns 1 --duration 40000 --recordramp

python ./tools/autorunexp-3.py -p "voterdemohstorefile" -o "experiments/0625/voterdemohstorefile-1c-95-0625-site03.txt" \
--txnthreshold 0.95 -e "experiments/0625/site03-0625-voterdemohstore-1.txt" --winconfig "(site03) ramp async no log h-store" \
--threads 1 --rmin 100 --rmax 700 --rstep 50 --finalrstep 50 --warmup 10000 --hstore --hscheduler --numruns 1 --recordramp

cd /home/jlmeehan/git/s-store

ant clean-all build-all

ant hstore-prepare -Dproject="voterdemosstorefile" -Dhosts="localhost:0:0"

python ./tools/autorunexp-3.py -p "voterdemosstorefile" -o "/home/jlmeehan/git/h-store/experiments/0625/voterdemosstore-1c-95-0625-site03.txt" \
--txnthreshold 0.95 -e "/home/jlmeehan/git/h-store/experiments/0625/site03-0625-voterdemosstore-1.txt" --winconfig "(site03) s-store log ramp" \
--threads 1 --rmin 1000 --rmax 5000 --rstep 200 --finalrstep 200 --warmup 10000 --numruns 1 --log --recordramp

python ./tools/autorunexp-3.py -p "voterdemosstorefile" -o "/home/jlmeehan/git/h-store/experiments/0625/voterdemosstore-1c-95-0625-site03.txt" \
--txnthreshold 0.95 -e "/home/jlmeehan/git/h-store/experiments/0625/site03-0625-voterdemosstore-1.txt" --winconfig "(site03) s-store no log ramp" \
--threads 1 --rmin 1000 --rmax 5000 --rstep 200 --finalrstep 200 --warmup 10000 --numruns 1 --recordramp

python ./tools/autorunexp-3.py -p "voterdemosstorefile" -o "/home/jlmeehan/git/h-store/experiments/0625/voterdemosstore-1c-95-0625-site03.txt" \
--txnthreshold 0.95 -e "/home/jlmeehan/git/h-store/experiments/0625/site03-0625-voterdemosstore-1.txt" --winconfig "(site03) s-store log" \
--threads 1 --rmin 1000 --rstep 1000 --finalrstep 100 --warmup 10000 --numruns 1 --log

python ./tools/autorunexp-3.py -p "voterdemosstorefile" -o "/home/jlmeehan/git/h-store/experiments/0625/voterdemosstore-1c-95-0625-site03.txt" \
--txnthreshold 0.95 -e "/home/jlmeehan/git/h-store/experiments/0625/site03-0625-voterdemosstore-1.txt" --winconfig "(site03) s-store no log" \
--threads 1 --rmin 1000 --rstep 1000 --finalrstep 100  --warmup 10000 --numruns 1