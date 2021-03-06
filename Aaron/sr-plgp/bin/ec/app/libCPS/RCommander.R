setwd("~")
java <-read.csv("out.txt",header=T,sep=",")

plot(Freq, Coherency, type="l",col="red")

theo <- read.csv("coh30m.dat",header=T,sep=",")

theo
attach(theo)

lines(Freq, Coherency, type="l")


###> colnames(theo)
###[1] "Freq"      "Coherency" "Imaginary" "Norm"

plot(FREQUENCY.Hz., C.KM.S., type="l")
lines (FREQUENCY.Hz., C.KM.S., type="l")

attach(java)
colnames(java)

theo9 <- read.csv("theo_coh9.dat",header=T,sep=",")
attach(theo9)
lines(Freq, Coherency, type="l",col=1)
lines(Freq, Coherency, type="l",col=3)
lines(Freq, Coherency, type="l",col=5)
