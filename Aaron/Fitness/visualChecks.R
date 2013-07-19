# Set up underlying measured model

setwd("~/workspace/Fitness/")
measured <- read.csv("site01_20m.dat",header=T,sep=",")
attach(measured)
plot(Freq, Coherency, type="l")



plotz <-read.csv("plot.txt",header=T,sep=",")
attach(plotz)
lines(Freq, Coherency, type="l",col=2)


java <-read.csv("out.txt",header=T,sep=",")
o1 <-read.csv("out1.txt",header=T,sep=",")
o2 <-read.csv("out2.txt",header=T,sep=",")

attach(o1)
attach(o2)

plot(Freq, Coherency, type="l",col="red")



theo


lines(Freq, Coherency, type="l")


###> colnames(theo)
###[1] "Freq"      "Coherency" "Imaginary" "Norm"

plot(FREQUENCY.Hz., C.KM.S., type="l")
lines (FREQUENCY.Hz., C.KM.S., type="l")
java <-read.csv("out.txt",header=T,sep=",")
attach(java)
colnames(java)

theo9 <- read.csv("theo_coh9.dat",header=T,sep=",")
attach(theo9)
lines(Freq, Coherency, type="l",col=1)
lines(Freq, Coherency, type="l",col=2)
lines(Freq, Coherency, type="l",col=3)
lines(Freq, Coherency, type="l",col=4)
lines(Freq, Coherency, type="l",col=5)
