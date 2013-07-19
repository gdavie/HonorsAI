#R is pretty cool for plotting, when googling it search for 'cran' or 'r-cran'
#I would seriously recommend learning some more plotting commands, as they will
#be necessary for your report.
#Depending on what you use to write your report (I used latex), the image format
#makes a difference. If you can use .eps that will give the best results but it's 
#not always an option.

#This script takes in 3 data files and plots them against each other.
#the first is the coherency measured from the site (The target)
#The second (in this case) is the calculated coherency from GNS
#the third (in this case) is the output from ESPAC.


#this is the directory where yout output files are located.
#setwd("~/4year/489/output/")

##########################SITE01###########################
#comment out the following line if you want the plot to screen.
#also the dev.off() line
jpeg(filename = "site06.jpg")
#one of the data files for comparison.
#it should be the same format as those provided, i.e. same header row 
#followed by two comma separated columns
#I think header=T is boolean, and the header will label the columns.
#sep is the columm seperator in the data (ie we have used CSV)
theo <- read.csv("plot.1BILL.txt",header=T,sep=",")
#this call 'attaches' the data we just read in for the 'plot' command
attach(theo)
#plot the data.
#type is type of plot (l=line) (i think)
#xlim limits the plot to under 20 khz
#col is colour (there are 9)
#lwd is line width
#lty is line type (solid, dot, dash, etc, I think there are 9 also).
#		It is useful to consider the plots in black and white, line type is useful there
plot(Freq, Coherency, type="l", xlab="Frequency(Hz)", xlim = c(0,20), col=1,lwd=1)
bill <-read.csv("plot.1BILL.txt",header=T,sep=",")
attach(bill)
lines(Freq, Coherency, type="l",col=2, lwd=3, lty=1) #RED
spac1 <-read.csv("plot.5.txt",header=T,sep=",")
attach(spac1)
lines(Freq, Coherency, type="l",col=3, lwd=3) #GREEN

#spac2 <-read.csv("plot.2.txt",header=T,sep=",")
#attach(spac2)
#lines(Freq, Coherency, type="l",col=4, lwd=3) #GREEN

#spac3 <-read.csv("plot.3.txt",header=T,sep=",")
#attach(spac3)
#lines(Freq, Coherency, type="l",col=5, lwd=3) #GREEN

#spac4 <-read.csv("plot.4.txt",header=T,sep=",")
#attach(spac4)
#lines(Freq, Coherency, type="l",col=6, lwd=3) #GREEN

#spac5 <-read.csv("plot.5.txt",header=T,sep=",")
#attach(spac5)
#lines(Freq, Coherency, type="l",col=7, lwd=3) #GREEN

#comment out the following line if you want the plot to screen.
#also the jpeg() line
dev.off()

#colours
#lines(Freq, Coherency, type="l",col=3, lwd=3) #GREEN
#lines(Freq, Coherency, type="l",col=4, lwd=3) #blue
#lines(Freq, Coherency, type="l",col=5, lwd=3)	#aqua
#lines(Freq, Coherency, type="l",col=6, lwd=3)	#purple
#lines(Freq, Coherency, type="l",col=7, lwd=3)	#yellow
#lines(Freq, Coherency, type="l",col=8, lwd=3)	#grey
#lines(Freq, Coherency, type="l",col=9, lwd=3)	#dark grey (black?)


