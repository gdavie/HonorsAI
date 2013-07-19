c######################################################################
c
c       bsort.f77
c       Part of the source required for libCPS, the library that
c       captures the relevant functionality from the CPS suite,
c       for use with the AI-SPAC project.
c
c       The original file comments follow this heading.
c
c       Aaron Scoble
c       Victoria University of Wellington
c       20/01/2013
c
c       This contains the bubble sort subroutine of sprep96.f
c       file from Herrmann's Computer Programs in Seismology (CPS).
c       It has been heavily modified for use with the AI-SPAC
c       system, and a lot of functionality has been stripped to
c       allow us to concentrate on what we need.
c       The comments have also been stripped out, and the interested
c       user is urged to refer to the original files.
c
c#####################################################################
c---------------------------------------------------------------------c
c                                                                     c
c      COMPUTER PROGRAMS IN SEISMOLOGY                                c
c      VOLUME V                                                       c
c                                                                     c
c      PROGRAM: SPREP96                                               c
c                                                                     c
c      COPYRIGHT 1996 R. B. Herrmann                                  c
c                                                                     c
c      Department of Earth and Atmospheric Sciences                   c
c      Saint Louis University                                         c
c      221 North Grand Boulevard                                      c
c      St. Louis, Missouri 63103                                      c
c      U. S. A.                                                       c
c                                                                     c
c---------------------------------------------------------------------c
c       Changes
c       17 OCT 2002 - Added description of dfile format to usage routine
c       09 SEP 2012 - correct last line of bsort from n=n-m to nn=n-m
c             Thanks to ruoshan at ustc
c-----
c       program to prepare input for sdisp96(III)
c-----
c----------------------------------------------------------------------c
        subroutine bsort(nn,x,isign)
c       do bubble sort.
c       isign=+1  increase   =-1 decrease.
c
          real*4 x(nn)
c
        n = nn
        m=0
        do 50 i=2,n
          ii=i-m-1
          do 40 j=ii,1,-1
            x0=x(j+1)-x(j)
              if(isign.le.0) x0=-x0
            if(abs(x0).lt.1.e-7) go to 20
C              if(x0) 10,20,50
            if(x0 .lt. 0.0)then
                go to 10
            else if(x0 .eq. 0.0)then
                go to 20
            else
                go to 50
            endif
10        continue
            x0=x(j)
            x(j)=x(j+1)
            x(j+1)=x0
            go to 40
20        continue
            m=m+1
            do 30 k=j,n-m
              x(k)=x(k+1)
30        continue
            go to 50
40      continue
50    continue
        nn=n-m
        return
        end

        subroutine npow2(npts)
c-----
c       Given npts, determine the N=2**m such that N >= npts
c       return the new ntps
c-----
        integer*4 nsamp, npts
        nsamp = npts
        npts = 1
 1000   continue
        if(npts.ge.nsamp)return
        npts = 2*npts
        go to 1000
        end

