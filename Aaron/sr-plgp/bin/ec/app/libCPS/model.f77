  !	  +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
         subroutine getmodel(model, nlayers)
c######################################################################
c
c       model.f77
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
c       This contains some of the functionality of igetmod.f
c       file from Herrmann's Computer Programs in Seismology (CPS).
c       It has been heavily modified for use with the AI-SPAC
c       system, and a lot of functionality has been stripped to
c       allow us to concentrate on what we need.
c       The comments have also been stripped out, and the interested
c       user is urged to refer to the original files.
c
c#####################################################################
c----------------------------------------------------------------------c
c                                                                      c
c      COMPUTER PROGRAMS IN SEISMOLOGY                                 c
c      VOLUME III                                                      c
c                                                                      c
c      PROGRAM: IGETMODEL                                               c
c                                                                      c
c      COPYRIGHT 1996, 2010                                            c
c      R. B. Herrmann                                                  c
c      Department of Earth and Atmospheric Sciences                    c
c      Saint Louis University                                          c
c      221 North Grand Boulevard                                       c
c      St. Louis, Missouri 63103                                       c
c      U. S. A.                                                        c
c                                                                      c
c----------------------------------------------------------------------c
c-----
c       HISTORY
c
c       09 08 2000  gave ierr an initial default value for g77
c       01 13 2001  put in close(lun) if file is not model file
c       03 MAY 2002     Modify to permit read from standard input
c       06 JUL 2005 moved inquire to permit use of STDIN
c
c-----
c	This subroutine not only derives the additional values, it
c	also assigns them to common variables. 
c
c	This call may be optimised out by replacement, performing
c	the calculations in java or c before passing the model through.
c       Looks like the /isomod/ commons need to be addressed though.
c---------------------------------------------------
         
         implicit none
         integer nlayers
	    real *8 model(*)
        
        integer*4 mmax
        integer*4 ierr
        character string*80
        logical listmd

        integer LIN, LOT, LER
        parameter (LIN=5,LOT=6,LER=0)

        integer NL, l
        parameter (NL=200)
        common/isomod/d(NL),vp(NL),vs(NL),rho(NL)
        real d,vp,vs, rho
        common/depref/refdep
        real refdep
        real tmp, sq3, intmp
        logical ext
        character ftype*80
        integer lun, j, i, irefdp

        !=======GLOBALS===============|
        common/azisomod/azd(NL),azvp(NL),azvs(NL),azrho(NL)
        real *8 azd,azvp,azvs, azrho

c ----
c           Reset these variables
c-----
        mmax = 0
        refdep = 0.0
        irefdp = 0
        
 
        l=1
 1000       continue
 	   intmp=model(l)
 	   if(intmp.eq.-9999)then 
 	   go to 9000
 	   else
            j = mmax +1
 !           write(lot,*), l, j
            d(j)=intmp
            l=l+1
            vs(j)=model(l)

                if(d(j).lt.0.0)then
                    d(j) = -d(j)
                    refdep = refdep + d(j)
                    irefdp = j
                endif
                azd(j)=d(j)
                azvs(j)=vs(j)
        !         write(LOT,*), azd(j),azvs(j)
            mmax = j
            l=l+1
            go to 1000
            endif
 9000       continue

            ierr = 0
            do 2000 i=1,mmax
c               calculate density, need temporary variable
c ----------------------------------
            tmp=0.05/vs(i)
            tmp=2.2-tmp
            rho(i)=max(1.0,tmp)
            azrho(i)=rho(i)
c------------------------------------
c               calculate v_p using temporary variables
c------------------------------------
            sq3=sqrt(3.0)
            tmp=sq3*vs(i)
            vp(i)=max(1.5, tmp)
            azvp(i)=vp(i)
c------------------------------------
                 
c	write(LOT,*)i,d(i),azd(i),vp(i),azvp(i),vs(i),azvs(i),rho(i),
c     1 		azrho(i) 
 2000       continue
        nlayers=mmax
        
	!write(6,*), azd(1), azvs(1)
        !write(6,*), azd(2), azvs(2)
        
        return
        end
    
