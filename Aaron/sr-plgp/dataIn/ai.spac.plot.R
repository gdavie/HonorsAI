setwd("~/workspace/Fitness/")

##########################SITE01 -- example ###########################
jpeg(filename="site01_ex.jpg")
theo <- read.csv("site01_20m.dat",header=T,sep=",")
attach(theo)
plot(Freq, Coherency, type="l", xlab="Frequency(Hz)", xlim = c(0,20), col=1,lwd=1)
bill <-read.csv("plot.1BILL.txt",header=T,sep=",")
attach(bill)
lines(Freq, Coherency, type="l",col=2, lwd=3, lty=1) #RED
dev.off()


##########################SITE01 -- layercmp ###########################
jpeg(filename="site01_cmp.pdf")
theo <- read.csv("site01_20m.dat",header=T,sep=",")
attach(theo)
plot(Freq, Coherency, type="l", xlab="Frequency(Hz)", xlim = c(0,20), col=1,lwd=1)
bill <-read.csv("plot.1BILL.txt",header=T,sep=",")
attach(bill)
lines(Freq, Coherency, type="l",col=2, lwd=3, lty=1) #RED
spac <-read.csv("plot.1_layer_cmp.txt",header=T,sep=",")
attach(spac)
lines(Freq, Coherency, type="l",col=3, lwd=3, lty=6) #GREEN
dev.off()

##########################SITE01###########################
jpeg(filename="site01.jpg")
theo <- read.csv("site01_20m.dat",header=T,sep=",")
attach(theo)
plot(Freq, Coherency, type="l", xlab="Frequency(Hz)", xlim = c(0,20), col=1,lwd=1)
bill <-read.csv("plot.1BILL.txt",header=T,sep=",")
attach(bill)
lines(Freq, Coherency, type="l",col=2, lwd=3, lty=1) #RED
spac <-read.csv("plot.1.txt",header=T,sep=",")
attach(spac)
lines(Freq, Coherency, type="l",col=3, lwd=3, lty=6) #GREEN
dev.off()

##########################SITE02###########################
jpeg(filename="site02.jpg")
theo <- read.csv("site02_20m.dat",header=T,sep=",")
attach(theo)
plot(Freq, Coherency, type="l", xlab="Frequency(Hz)", xlim = c(0,20), col=1,lwd=1)
bill <-read.csv("plot.2BILL.txt",header=T,sep=",")
attach(bill)
lines(Freq, Coherency, type="l",col=2, lwd=3, lty=1) #RED
spac <-read.csv("plot.2.txt",header=T,sep=",")
attach(spac)
lines(Freq, Coherency, type="l",col=3, lwd=3, lty=6) #GREEN
dev.off()

##########################SITE03###########################
jpeg(filename="site03.jpg")
theo <- read.csv("site03_15m.dat",header=T,sep=",")
attach(theo)
plot(Freq, Coherency, type="l", xlab="Frequency(Hz)", xlim = c(0,20), col=1,lwd=1)
bill <-read.csv("plot.3BILL.txt",header=T,sep=",")
attach(bill)
lines(Freq, Coherency, type="l",col=2, lwd=3, lty=1) #RED
spac <-read.csv("plot.3.txt",header=T,sep=",")
attach(spac)
lines(Freq, Coherency, type="l",col=3, lwd=3, lty=6) #GREEN
dev.off()

##########################SITE04###########################
jpeg(filename="site04.jpg")
theo <- read.csv("site04_20m.dat",header=T,sep=",")
attach(theo)
plot(Freq, Coherency, type="l", xlab="Frequency(Hz)", xlim = c(0,20), col=1,lwd=1)
bill <-read.csv("plot.4BILL.txt",header=T,sep=",")
attach(bill)
lines(Freq, Coherency, type="l",col=2, lwd=3, lty=1) #RED
spac <-read.csv("plot.4.txt",header=T,sep=",")
attach(spac)
lines(Freq, Coherency, type="l",col=3, lwd=3, lty=6) #GREEN
dev.off()

##########################SITE04b###########################
jpeg(filename="site04b.jpg")
theo <- read.csv("site04_20m.dat",header=T,sep=",")
attach(theo)
plot(Freq, Coherency, type="l", xlab="Frequency(Hz)", xlim = c(0,20), col=1,lwd=1)
bill <-read.csv("plot.4BILL.txt",header=T,sep=",")
attach(bill)
lines(Freq, Coherency, type="l",col=2, lwd=3, lty=1) #RED
spac <-read.csv("plot.4b.txt",header=T,sep=",")
attach(spac)
lines(Freq, Coherency, type="l",col=3, lwd=3, lty=6) #GREEN
dev.off()

##########################SITE05###########################
jpeg(filename="site05.jpg")
theo <- read.csv("site05_20m.dat",header=T,sep=",")
attach(theo)
plot(Freq, Coherency, type="l", xlab="Frequency(Hz)", xlim = c(0,20), col=1,lwd=1)
bill <-read.csv("plot.5BILL.txt",header=T,sep=",")
attach(bill)
lines(Freq, Coherency, type="l",col=2, lwd=3, lty=1) #RED
spac <-read.csv("plot.5.txt",header=T,sep=",")
attach(spac)
lines(Freq, Coherency, type="l",col=3, lwd=3, lty=6) #GREEN
dev.off()

#========================================================


##########################SITE05###########################
jpeg(filename = "benmore.jpg")
theo <- read.csv("sinm_benmore_coh20m.dat",header=T,sep=",")
attach(theo)
plot(Freq, Coherency, type="l", xlab="Frequency(Hz)", xlim = c(0,20), col=1,lwd=1)
bill <-read.csv("plot.benBILL.txt",header=T,sep=",")
attach(bill)
lines(Freq, Coherency, type="l",col=2, lwd=3, lty=1) #RED
spac <-read.csv("plot.ben.txt",header=T,sep=",")
attach(spac)
lines(Freq, Coherency, type="l",col=3, lwd=3, lty=6) #GREEN
dev.off()

#========================================================

lines(Freq, Coherency, type="l")
attach(spac)
lines(Freq, Coherency, type="l",col=3, lwd=3) #GREEN

lines(Freq, Coherency, type="l",col=2, lwd=3) #RED
lines(Freq, Coherency, type="l",col=3, lwd=3) #GREEN
lines(Freq, Coherency, type="l",col=4, lwd=3) #blue
lines(Freq, Coherency, type="l",col=5, lwd=3)	#aqua
lines(Freq, Coherency, type="l",col=6, lwd=3)	#purple
lines(Freq, Coherency, type="l",col=7, lwd=3)	#yellow
lines(Freq, Coherency, type="l",col=8, lwd=3)	#grey
lines(Freq, Coherency, type="l",col=9, lwd=3)	#dark grey (black?)


