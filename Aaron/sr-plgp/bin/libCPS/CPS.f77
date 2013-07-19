c######################################################################
c
c       CPS.f77
c       Part of the source required for libCPS, the library that
c       captures the relevant functionality from the CPS suite,
c       for use with the AI-SPAC project.
c
c       The original file comments for sprep and sdepgn
c           follow this heading. The original file comments for
c           sdisp and sregn are found in the xxSub.f77 files.
c
c       Aaron Scoble
c       Victoria University of Wellington
c       20/01/2013
c
c       This contains some of the functionality of the following
c       files from Herrmann's Computer Programs in Seismology (CPS):
c
c       sdisp96.f , sprep96.f, sregn96.f, sdepgn96.f
c
c       They have been heavily modified for use with the AI-SPAC
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
c                                                                      c
c      COMPUTER PROGRAMS IN SEISMOLOGY                                 c
c      VOLUME III                                                      c
c                                                                      c
c      PROGRAM: SDPEGN96                                               c
c                                                                      c
c      COPYRIGHT 1996                                                  c
c      R. B. Herrmann, Chien-Ying Wang                                 c
c      Department of Earth and Atmospheric Sciences                    c
c      Saint Louis University                                          c
c      221 North Grand Boulevard                                       c
c      St. Louis, Missouri 63103                                       c
c      U. S. A.                                                        c
c                                                                      c
c----------------------------------------------------------------------c
c Revision history:
c       27 DEC 2000 - removed code commented out
c       13 NOV 2001 - ensured that all modes are out for -S option
c       17 NOV 2003 - add option to plot ARE ALE (-A0) or
c               ARE sqrt(c) ALE sqrt(c) for -Ac for SPAC
c       20 JUL 2004 - changed order of declaration of NOBS in outplt
c       28 MAY 2007 - -TXT -M together give debug output
c       13 MAY 2010 - added -NOBLACK to prevent black outline around
c                    observed dispersion for compatibility with sdpdsp96
c       11 MAY 2011 - added -TICONLY to permit tics but no annotation
c                   - added -ONLYKM to label axis velocty but no C or U
c----------------------------------------------------------------------c

        ! Dummy program, need this to create the JNI library.
        program CPS
        call main()
        end
        
        subroutine main()
        
       !================================================
       !	Build some global variables to redude the file handling
       !=================================================
       ! ========= Hold the list of frequencies ==========|
       	parameter(NPERIOD=4090)
        common/freq/fval
        real fval(NPERIOD)    
        ! ========== data for modelling ================|
	integer numlayers	
	! return values
        common /azout/ azc, azrur0, azf0
	real*8 azc(NPERIOD), azrur0(NPERIOD),azf0(NPERIOD),out(NPERIOD)
	
	integer rv(3)

 40   format(3g13.5)	
 
        call sprep()
        call sdisp()
	call sregn()
	
        end	
        
   !++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	subroutine getfreq(out)
	parameter(NPERIOD=4090)
	common /azout/ azc, azrur0, azf0
	real*8 azc(NPERIOD), azrur0(NPERIOD),azf0(NPERIOD), out(NPERIOD)

	out=azf0
	end

!+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	subroutine getc(out)
	parameter(NPERIOD=4090)
	common /azout/ azc, azrur0, azf0
	real*8 azc(NPERIOD), azrur0(NPERIOD),azf0(NPERIOD), out(NPERIOD)

	out=azc
	end
	
!+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	subroutine getellip(out)
	parameter(NPERIOD=4090)
	common /azout/ azc, azrur0, azf0
	real*8 azc(NPERIOD), azrur0(NPERIOD),azf0(NPERIOD), out(NPERIOD)

	out=azrur0
	end
	
  !+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
  !FILE:SPREP
      subroutine sprep(arr, model)
      !-----------------------------------------------------------------
	real *8 model(*), arr(*)
	real tmp
        parameter(NPERIOD=4090)
        common/freq/fval(NPERIOD)
        !real fval(NPERIOD)
        integer nfval, ctr
        common/pari/ mmax
        !integer mmax
        
        !======= GLOBALS ========|
        common /azmodl/ numlayers	
	!integer numlayers	
 ! TODO nfval is the number of frequencies from the file, it may be less
 !	than NPERIOD.

        	!=====================================================
        	!	Use the provided array to create the list of frequencies
        	!	Any frequencies of 0 or less are dropped.
        	!=====================================================
	nfval = 0
	ctr=1
 1000   continue	    
 	    tmp=arr(ctr)
            if(tmp.le.0.0)go to 1000
            nfval = nfval + 1
            fval(nfval) = tmp
            ctr=ctr+1
            if(ctr.ge.4090)go to 9999
            go to 1000
 9999   continue
  	  !===================================================
  	  ! 	Use the provided array to assign the model to common 
  	  !	Variables. This may be better if replaced with passing of 
  	  !	references instead of common vars.
  	  !==================================================
      	call getmodel(model, numlayers)
      	mmax=numlayers
        call bsort(nfval,fval,-1)
        	!=============================================
        	!	removing the following kills it for some reason???
        	!=============================================
        open(1,file='killMe.txt',form='formatted',
     1      access='sequential',status='unknown')
       close (1)
       return
         end                                
!+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 !FILE::sdisp 
  
	subroutine sdisp()

        implicit double precision (a-h,o-z)
        integer LIN, LOT, NL, NL2
        parameter (LIN=5, LOT=6, NL=200, NL2=NL+NL)
        equivalence(vth(1),vts(1,1)),(vtp(1),vts(1,2))
        real*4 vth(NL2),vtp(NL2)
        real*4 vts(NL2,2)
        common/modl/ d,a,b,rho
        real*4 d(NL),a(NL),b(NL),rho(NL)
        common/pari/ mmax,mode
        integer mmax, mode
        common/water/iwat(NL)
        integer iwat
        common/pard/ twopi,displ,dispr
        common/vels/ mvts(2),vts

        parameter(NPERIOD=4090)
        common/freq/fval
        real *8 fval(NPERIOD)
        integer nfval
        
        !======= GLOBALS ========|
        common /azmodl/ numlayers	
	!integer numlayers	
	  !=======GLOBALS===============|
        common/azisomod/azd(NL),azvp(NL),azvs(NL),azrho(NL)
        !real azd,azvp,azvs, azrho
c-----
c       this is the model space for the determination of
c       dispersion for an isotropic medium
c-----
        common/isomod/od(NL),oa(NL),ob(NL),orho(NL),
     1      qa(NL),qb(NL),etap(NL),etas(NL), 
     2      frefp(NL), frefs(NL)
        common/depref/refdep
        real*4 refdep
        real*4 od, oa, ob, orho, qa, qb, etap, etas, frefp, frefs
        character title*80
        character mname*80
        real*4 dt
!        logical verby
 !         verby=.false.
c-----
c       main program just does the control.
c-----
        twopi=6.283185307179586d+00	  
	 dt=-1
	 npts=NPERIOD
	 n1=-1
	 n2=-2
	 nmodes=1
	 mode = nmodes
	 faclov=5.0
	 displ = faclov
	 facray=5.0
	 dispr = facray
          nfval = npts
        ierr = 0	!  Trigger no-error condition. maybe superfluous
        idispl = 0
        idispr = 1

        !write(6,*), azd
        
        

	!write(6,*), '-----call disprs'
	ilvry=2
           call disprs(ilvry,dt,npts,iret,mname,
     1                  .false.,nfval,fval,ccmin,ccmax,numlayers)
     
       !  write(6,*), '-----return disprs'
        return
        end
!+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
  !FILE:SREGN

        subroutine sregn()
	
        implicit none
c-----
c       common blocks
c-----
        integer NL
        parameter(NL=200)
c-----
c       NL  - number of layers in model
c-----
        common/isomod/di(NL),ai(NL),bi(NL),rhoi(NL),
     1      qai(NL),qbi(NL),etapi(NL),etasi(NL), 
     2      frefpi(NL), frefsi(NL)
        real*4 di, ai, bi, rhoi, qai, qbi, etapi, etasi, frefpi, frefsi

        common/model/  d(NL),a(NL),b(NL),rho(NL),qa(NL), qb(NL)
        real*4 d, a, b, rho, qa, qb
        common/depref/refdep
        real*4 refdep
        common/dmodl/  zd(NL),za(NL),zb(NL),zrho(NL),zqai(NL),zqbi(NL),
     1      zetap(NL),zetas(NL),zfrefp(NL),zfrefs(NL),
     2      xmu(NL), xlam(NL), mmax
        real*8 zd, za, zb, zrho, zqai, zqbi, zetap, zetas, 
     1      zfrefp, zfrefs, xmu, xlam
        integer mmax

        common/eigfun/ ur(NL),uz(NL),tz(NL),tr(NL),uu0(4),
     *                 dcda(NL),dcdb(NL),dcdr(NL),dcdh(NL)
        real*8 ur, uz, tz, tr, uu0, dcda, dcdb, dcdr, dcdh

        real*4 sdcda(NL), sdcdb(NL), sdcdh(NL), sdcdr(NL) 
        real*4 spur(NL), sptr(NL), spuz(NL), sptz(NL)

        common/wateri/iwat(NL)
        integer iwat
c-----
c       model characterization
c-----
        common/modspec/allfluid
        logical allfluid

        common/sumi/   sumi0,sumi1,sumi2,sumi3,flagr,are,ugr
        real*8 sumi0, sumi1, sumi2, sumi3, flagr, are, ugr

        common/sphere2/vtp(NL),dtp(NL),rtp(NL)
        real*4 vtp,dtp,rtp
c-----
c       function prototype
c-----
        integer lgstr

c-----
c       local variables
c-----
        integer LER, LIN, LOT
        parameter(LER=0,LIN=5,LOT=6)
        integer ipar(10)
        integer nipar(10)
        integer i,j,k,l
        integer iunit,iiso,iflsph,idimen,icnvel,ierr
        integer MAXMOD
        parameter(MAXMOD=1)
        integer lss, lsso, lrr, lrro, n1, n2, npts, ifirst
        integer ifunc, mode, nsph, nper, nmodes, mmaxot

        logical ext
        logical nwlyrs, nwlyrr
        logical dolove, dorayl

        character mname*120
        character title*120 
        character*12 fname(2)

        real*4 fpar(10)
        real*4 s1, dephs, dephr, faclov, facray, hr, hs, dt
        real*4 deplw, depthr, depths, depup
        real*4 ohs, ohr
        real*4 rare, rtr, rtz, rur0, rur, ruz
        real*4 sare, sd2ur, sd2uz, sduz, sur, sur0, suz, sdur
        real*4 sum, sumgr, sumgv, sumkr
        real*4 wvnsrc, wvnrec, xl2m
        real*4 twopi

        real*8 durdz, duzdz, d2urdz, d2uzdz
        real*8 rurdz, ruzdz
        real*8 c, omega, wvno, gammar, csph, usph
        real*8 cp(MAXMOD), t

        !======= GLOBALS ========|
        ! model file replacement
       common /azmodl/ numlayers	
	integer numlayers	, ii
	! egn file replacement
	integer NPERIOD
	parameter(NPERIOD=4090)
	common /azhdr/ azfunc,azmode,azparam
	real*4 azparam(NPERIOD), f0
	integer azfunc(NPERIOD), azmode(NPERIOD), hdrcount
	 ! gtsmdl removal
        common/azisomod/azd(NL),azvp(NL),azvs(NL),azrho(NL)
        real *8 azd,azvp,azvs, azrho
        common /azphas/azcp,azt0   
        real*8 azcp(NPERIOD)
        real*8 azt0(NPERIOD)
        ! return values
        common /azout/azc, azrur0, azf0
	real*8 azc(NPERIOD), azrur0(NPERIOD),azf0(NPERIOD)
 30   format(' FREQUENCY(Hz)  C(KM/S) ELLIPTICITY')
 40   format(3g13.5)
	   
        hs = -1.0E+21
        hr = -1.0E+21
        nipar(4) = 0
        nipar(5) = 0
        nipar(6) = 0
        nipar(7) = 0
        nipar(8) = 0
         dt=-1
	 npts=NPERIOD
	 n1=-1
	 n2=-2
	 dolove=.false.
	 dorayl=.true.
	 hs=0
	 hr=0
	 nmodes=1
	 faclov=5.0
	 facray=5.0
!	 mname='MODEL.FIT'
	 
	mmax=numlayers
	iunit=0
	iiso=0
	iflsph=0
	idimen=1
	icnvel=0
	ierr=0

! c-----
c       check for water only
c-----
        allfluid = .true.
        do 1200 i=1,mmax
            if(bi(i).gt.0.0)then
                allfluid = .false.
            endif
 1200   continue
c-----
c       get the Q information into the program
c       since this is not carried through in the sdisp96 output
c-----
        do 1234 i=1,mmax
            if(qai(i).gt.1.0)qai(i)=1.0/qai(i)
            if(qbi(i).gt.1.0)qbi(i)=1.0/qbi(i)
            zqai(i) = qai(i)
            zqbi(i) = qbi(i)
            if(frefpi(i).le.0.0)frefpi(i) = 1.0
            if(frefsi(i).le.0.0)frefsi(i) = 1.0
            zfrefp(i) = frefpi(i)
            zfrefs(i) = frefsi(i)
            zetap(i) = etapi(i)
            zetas(i) = etasi(i)
 1234   continue
        nsph = iflsph
c-----
c       get source depth, and sphericity control
c-----
        if(hs.lt.-1.0E+20)then
            depths = ohs
        else
            depths = hs
        endif
        if(hr.lt.-1.0E+20)then
            depthr = ohr
        else
            depthr = hr
        endif
        ! TODO superfluous calls and assignments here now
        depthr = depthr + refdep
        depths = depths + refdep
        dephs = depths
        dephr = depthr
        
       ! write(lot,30) ! write header
c-----
c       define modulus of rigidity, also get current transformed model
c       parameters
c       set fpar(1) = refdep
c       set ipar(1) = 1 if medium is spherical
c       set ipar(2) = 1 if source is in fluid
c       set ipar(3) = 1 if receiver is in fluid
c-----
        deplw = 0.0
        depup = 0.0
        ipar(2) = 0
        ipar(3) = 0
        do 185 i=1,mmax
            zd(i) = azd(i)
            za(i) = azvp(i)
            zb(i) = azvs(i)
            zrho(i) = azrho(i)
                xmu(i)=dble(rho(i)*b(i)*b(i))
            xlam(i)=dble(rho(i)*(a(i)*a(i)-2.*b(i)*b(i)))
            depup = deplw + d(i)
            if(b(i) .lt. 0.0001*a(i))then
                iwat(i) = 1
                if(depths.ge.deplw .and. depths.lt.depup)then
                    ipar(2) = 1
                endif
                if(depthr.ge.deplw .and. depthr.lt.depup)then
                    ipar(3) = 1
                endif
            else
                iwat(i) = 0
            endif
            deplw = depup
  185   continue
        do 186 i=4,8
            ipar(i) = nipar(i)
  186   continue
c-----
c       split a layer at the source depth
c       the integer ls gives the interface at which the eigenfunctions
c       are to be defined
c-----
        call insert(dephs,nwlyrs,lsso)
        call insert(dephr,nwlyrr,lrro)
        call srclyr(dephs,lss)
        call srclyr(dephr,lrr)

        twopi=2.*3.141592654
        ifirst = 0
        hdrcount=1
  400   continue
	ifunc=2
	mode=1
        t=azt0(hdrcount)

        s1=t

        omega=twopi/t
        if(hdrcount.gt.NPERIOD) go to 700
        cp = azcp(hdrcount)
        do 600 k=1,mode
                c=cp(k)
c-----
c       main part.
c-----
            wvno=omega/c
            call svfunc(omega,wvno)
            call energy(omega,wvno,mmax)
c-----
c       the gamma routine will use the spherical model, but the
c       frequency dependence and Q of the original model
c-----
                call gammap(omega,wvno,gammar)
                c = omega/wvno
c------
c     also check for possible conversion errors in IEEE
c     conversion from double precision to single precision
c-----
            if(dabs(uu0(1)).lt.1.0d-36)uu0(1)=0.0d+00
            if(dabs(uu0(3)).lt.1.0d-36)uu0(3)=0.0d+00
            if(dabs(c).lt.1.0d-36)c=0.0d+00
            if(dabs(ugr).lt.1.0d-36)ugr=0.0d+00
            if(dabs(are).lt.1.0d-36)are=0.0d+00
            if(dabs(flagr).lt.1.0d-36)flagr=0.0d+00
            if(dabs(gammar).lt.1.0d-36)gammar=0.0d+00

c-----
c       output necessary eigenfunction values for
c       source excitation
c-----
            mmaxot = mmax
c-----
c      get the derivatives of the eitenfunctions required
c      for source excitation from the definition of stress. For
c      completeness get the second derivative from the first
c      derivatives and the equation of motion for the medium
c-----
            xl2m = xlam(lss) + 2.0d+00 * xmu(lss)
            duzdz = ( tz(lss) + wvno*xlam(lss)*ur(lss))/xl2m
            if(iwat(lss).eq.1)then
                durdz = wvno*uz(lss)
                d2urdz = wvno*duzdz
            else
                durdz = -wvno*uz(lss) + tr(lss)/xmu(lss)
                d2urdz = (-wvno*duzdz*(xlam(lss)+xmu(lss)) 
     1              - ur(lss)*(zrho(lss)*omega*omega -
     2              wvno*wvno*xl2m))/xmu(lss)
            endif
            d2uzdz = ( - uz(lss) *( zrho(lss)*omega*omega - 
     1          xmu(lss)*wvno*wvno) + 
     2          wvno*durdz*(xlam(lss)+xmu(lss)))/ xl2m
            if(dabs(duzdz).lt.1.0d-36)duzdz=0.0d+00
            if(dabs(durdz).lt.1.0d-36)durdz=0.0d+00
            if(dabs(d2uzdz).lt.1.0d-36)d2uzdz=0.0d+00
            if(dabs(d2urdz).lt.1.0d-36)d2urdz=0.0d+00

            xl2m = xlam(lrr) + 2.0d+00 * xmu(lrr)
            ruzdz = ( tz(lrr) + wvno*xlam(lrr)*ur(lrr))/xl2m
            if(iwat(lrr).eq.1)then
                rurdz = wvno*uz(lrr)
            else
                rurdz = -wvno*uz(lrr) + tr(lrr)/xmu(lrr)
            endif
            if(dabs(ruzdz).lt.1.0d-36)ruzdz=0.0d+00
            if(dabs(rurdz).lt.1.0d-36)rurdz=0.0d+00

            sur = sngl(ur(lss))
            sdur = sngl(durdz)
            sd2ur = sngl(d2urdz)
            suz = sngl(uz(lss))
            sduz = sngl(duzdz)
            sd2uz = sngl(d2uzdz)
            sare = sngl(are)
            wvnsrc = sngl(wvno)
            sur0 = sngl(uu0(1))

            rur = sngl(ur(lrr))
            rtr = sngl(tr(lrr))
            ruz = sngl(uz(lrr))
            rtz = sngl(tz(lrr))
            rare = sngl(are)
            wvnrec = sngl(wvno)
            rur0 = sngl(uu0(1))

            sumkr = 0.0
            sumgr = 0.0
            sumgv = 0.0

     		omega = 6.2831853/s1
    		if(sngl(ugr).gt.0.0)then
                f0=1./s1
                c=omega/sngl(wvno)
 !               if(f0.eq.0.0)write(6,*),'**********'
                	azf0(hdrcount)=f0
                	azc(hdrcount)=c
                	azrur0(hdrcount)=rur0
                	!hdrcount=hdrcount+1
                	!======== These are the values we want =============|
  !                write(lot,*)'*****************'
   !                write(lot,40) f0,c,rur0! write to screen
               else
 !              	       write(lot,*) 'Error'
               	     !  go to 9999
               endif
  600   continue
  	hdrcount=hdrcount+1
        go to 400
  700   continue

 9999   continue
 	return
        end

