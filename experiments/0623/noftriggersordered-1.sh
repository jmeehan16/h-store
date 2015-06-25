#!/bin/bash
ant clean-all build-all

ant hstore-prepare -Dproject="microexpnoftriggerstrig4" -Dhosts="localhost:0:0"
ant hstore-prepare -Dproject="microexpnoftriggerstrig7" -Dhosts="localhost:0:0"
ant hstore-prepare -Dproject="microexpnoftriggerstrig10" -Dhosts="localhost:0:0"

python ./tools/autorunexp-3.py -p "microexpnoftriggerstrig4" -o "experiments/0623/microexpnoftriggerstrig4-1c-95-0623-site03.txt" \
--txnthreshold 0.95 -e "experiments/0623/site03-0623-noftriggers-1.txt" --winconfig "(site03)" \
--threads 1 --rmin 1000 --rstep 1000 --finalrstep 100 --warmup 10000 --hstore --hscheduler --numruns 1

python ./tools/autorunexp-3.py -p "microexpnoftriggerstrig7" -o "experiments/0623/microexpnoftriggerstrig7-1c-95-0623-site03.txt" \
--txnthreshold 0.95 -e "experiments/0623/site03-0623-noftriggers-1.txt" --winconfig "(site03)" \
--threads 1 --rmin 1000 --rstep 1000 --finalrstep 100 --warmup 10000 --hstore --hscheduler --numruns 1

python ./tools/autorunexp-3.py -p "microexpnoftriggerstrig10" -o "experiments/0623/microexpnoftriggerstrig10-1c-95-0623-site03.txt" \
--txnthreshold 0.95 -e "experiments/0623/site03-0623-noftriggers-1.txt" --winconfig "(site03)" \
--threads 1 --rmin 1000 --rstep 1000 --finalrstep 100 --warmup 10000 --hstore --hscheduler --numruns 1

python ./tools/autorunexp-3.py -p "microexpnoftriggerstrig4" -o "experiments/0623/microexpnoftriggerstrig4-1c-95-0623-site03.txt" \
--txnthreshold 0.95 -e "experiments/0623/site03-0623-noftriggers-log-1.txt" --winconfig "(site03)" \
--threads 1 --rmin 1000 --rstep 1000 --finalrstep 100 --warmup 10000 --hstore --hscheduler --numruns 1 --log

python ./tools/autorunexp-3.py -p "microexpnoftriggerstrig7" -o "experiments/0623/microexpnoftriggerstrig7-1c-95-0623-site03.txt" \
--txnthreshold 0.95 -e "experiments/0623/site03-0623-noftriggers-log-1.txt" --winconfig "(site03)" \
--threads 1 --rmin 1000 --rstep 1000 --finalrstep 100 --warmup 10000 --hstore --hscheduler --numruns 1 --log

python ./tools/autorunexp-3.py -p "microexpnoftriggerstrig10" -o "experiments/0623/microexpnoftriggerstrig10-1c-95-0623-site03.txt" \
--txnthreshold 0.95 -e "experiments/0623/site03-0623-noftriggers-log-1.txt" --winconfig "(site03)" \
--threads 1 --rmin 1000 --rstep 1000 --finalrstep 100 --warmup 10000 --hstore --hscheduler --numruns 1 --log