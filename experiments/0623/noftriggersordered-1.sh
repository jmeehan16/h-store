#!/bin/bash
ant clean-all build-all

ant hstore-prepare -Dproject="microexpnoftriggersorderedtrig4" -Dhosts="localhost:0:0"
ant hstore-prepare -Dproject="microexpnoftriggersorderedtrig7" -Dhosts="localhost:0:0"
ant hstore-prepare -Dproject="microexpnoftriggersorderedtrig10" -Dhosts="localhost:0:0"


python ./tools/autorunexp-3.py -p "microexpnoftriggersorderedtrig4" -o "experiments/0623/microexpnoftriggersorderedtrig4-1c-95-0623-site03.txt" \
--txnthreshold 0.95 -e "experiments/0623/site03-0623-noftriggersordered-2.txt" --winconfig "(site03)" \
--threads 1 --rmin 100 --rstep 100 --finalrstep 10 --warmup 10000 --hstore --hscheduler --numruns 1

python ./tools/autorunexp-3.py -p "microexpnoftriggersorderedtrig7" -o "experiments/0623/microexpnoftriggersorderedtrig7-1c-95-0623-site03.txt" \
--txnthreshold 0.95 -e "experiments/0623/site03-0623-noftriggersordered-2.txt" --winconfig "(site03)" \
--threads 1 --rmin 100 --rstep 100 --finalrstep 10 --warmup 10000 --hstore --hscheduler --numruns 1

python ./tools/autorunexp-3.py -p "microexpnoftriggersorderedtrig10" -o "experiments/0623/microexpnoftriggersorderedtrig10-1c-95-0623-site03.txt" \
--txnthreshold 0.95 -e "experiments/0623/site03-0623-noftriggersordered-2.txt" --winconfig "(site03)" \
--threads 1 --rmin 100 --rstep 100 --finalrstep 10 --warmup 10000 --hstore --hscheduler --numruns 1

