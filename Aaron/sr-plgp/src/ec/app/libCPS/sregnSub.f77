c######################################################################
c
c       sregnSub.f77
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
c       This contains some of the functionality of sregn96.f
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
c      PROGRAM: SREGN96                                                c
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
c
c
c     This program calculates the group velocity and partial
c     derivatives of Love waves for any plane multi-layered
c     model.  The propagator-matrix, instead of numerical-
c     integration method is used, in which the Haskell rather
c     than Harkrider formalisms are concerned.
c
c     Developed by C. Y. Wang and R. B. Herrmann, St. Louis
c     University, Oct. 10, 1981.  Modified for use in surface
c     wave inversion, with addition of spherical earth flattening
c     transformation and numerical calculation of group velocity
c     partial derivatives by David R. Russell, St. Louis
c     University, Jan. 1984.
c
c     Rewrite of theory to agree more with hspec96 wavenumber
c     integration code
c
c- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
c Revision history:
c       07 AUG 2002 - make string lengths 120 characters from 80
c       13 OCT 2006 - verbose output of energy integrals
c       26 SEP 2008 - fixed undefined LOT in subroutine up
c       14 JUN 2009 - reformulate in general terms as per book
c       01 AUG 2010 - change code to agree with book  for
c                     migration to TI. Also clean up code using
c                     implicit none
c       20 SEP 2012 - cleaned up some compiler warnings
c- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        subroutine insert(dph,newlyr,ls)
        implicit none
c-----
c       procdure arguments
c-----
        real*4 dph
        logical newlyr
        integer ls
c-----
c       common blocks
c-----
        integer NL
        parameter(NL=200)
        common/dmodl/  zd(NL),za(NL),zb(NL),zrho(NL),zqai(NL),zqbi(NL),
     1      zetap(NL),zetas(NL),zfrefp(NL),zfrefs(NL),
     2      xmu(NL), xlam(NL), mmax
        real*8 zd, za, zb, zrho, zqai, zqbi, zetap, zetas, 
     1      zfrefp, zfrefs, xmu, xlam
        integer mmax
        integer iwat
        common/wateri/iwat(NL)
c-----
c       local variables
c------
        integer LER, LIN, LOT
        parameter (LER=0, LIN=5, LOT=6)
        real*8 dep, dp, dphh, hsave
        integer m, m1

        dep = 0.0
        dp = 0.0
        dphh=-1.0
        ls = 1
        do 100 m =1, mmax
                dp = dp + zd(m)
                dphh = dp - dph
                if(m.eq.mmax)then
                        if(zd(mmax).le.0.0d+00 .or. dphh.lt.0.0)then
                                zd(mmax) = (dph - dp)
                        endif
                endif
                dep = dep + zd(m)
                dphh = dep - dph
                ls = m
                if(dphh.ge.0.0) go to 101
  100   continue
  101   continue
c-----
c       In the current model, the depth point is in the ls layer
c       with a distance dphh to the bottom of the layer
c
c       Do not create unnecessary layers, e.g., 
c            at surface and internally
c       However do put in a zero thickness layer 
c            at the base if necessary
c-----
        if(dph .eq. 0.0)then
            newlyr = .false.
                return
        else if(dphh .eq. 0.0 .and. ls.ne.mmax)then
            ls = ls + 1
            newlyr = .false.
                return
        else
            newlyr = .true.
c-----
c               adjust layering
c-----
                 do 102 m = mmax,ls,-1
                       m1=m+1
                        zd(m1) = zd(m)
                        za(m1) = za(m)
                        zb(m1) = zb(m)
                        zrho(m1) = zrho(m)
                        zqai(m1) = zqai(m)
                        zqbi(m1) = zqbi(m)
                        zfrefp(m1) = zfrefp(m)
                        zfrefs(m1) = zfrefs(m)
                        zetap(m1) = zetap(m)
                        zetas(m1) = zetas(m)
                        xmu(m1)=xmu(m)
                        xlam(m1)=xlam(m)
                iwat(m1) = iwat(m)
  102           continue
                hsave=zd(ls)
                zd(ls) = hsave - dphh
                zd(ls+1) = dphh
                mmax = mmax + 1
        endif
        return
        end

        subroutine srclyr(depth,lmax)
        parameter (LER=0, LIN=5, LOT=6)
        parameter(NL=200)
        common/dmodl/  zd(NL),za(NL),zb(NL),zrho(NL),zqai(NL),zqbi(NL),
     1      zetap(NL),zetas(NL),zfrefp(NL),zfrefs(NL),
     2      xmu(NL), xlam(NL), mmax
        real*8 zd, za, zb, zrho, zqai, zqbi, zetap, zetas, 
     1      zfrefp, zfrefs, xmu, xlam
        common/wateri/iwat(NL)
c-----
c       Find source/receiver boundary. It is assumed that
c       it will lie upon a boundary
c
c       lmax = source layer 
c       depth = source depth 
c-----
        dep = 0.0
        do 100 i=1,mmax
            if(abs(depth - dep).le.0.001*zd(i))then
                lmax = i
                return
            endif
            dep = dep + zd(i)
  100   continue
        return
        end 

        subroutine collap(ls,mmaxot)
        implicit none
c-----
c       routine arguments
c-----
        integer ls, mmaxot
c-----
c       common blocks
c-----
        integer NL
        parameter(NL=200)
        common/eigfun/ ur(NL),uz(NL),tz(NL),tr(NL),uu0(4),
     *                 dcda(NL),dcdb(NL),dcdr(NL),dcdh(NL)
        real*8 ur, uz, tz, tr, uu0, dcda, dcdb, dcdr, dcdh
c-----
c       local arguments
c-----
        integer i

        do 501 i = ls-1,mmaxot
            if(i .eq. ls -1)then
                dcda(i) = dcda(i) + dcda(i+1)
                dcdb(i) = dcdb(i) + dcdb(i+1)
                dcdr(i) = dcdr(i) + dcdr(i+1)
            endif
            if(i.gt.ls)then
                dcda(i-1) = dcda(i)
                dcdb(i-1) = dcdb(i)
                dcdh(i-1) = dcdh(i)
                dcdr(i-1) = dcdr(i)
                ur(i-1) = ur(i)
                tr(i-1) = tr(i)
                uz(i-1) = uz(i)
                tz(i-1) = tz(i)
            endif
  501   continue
        mmaxot = mmaxot - 1
        return
        end

        subroutine chksiz(dp,sp,mmaxot)
c-----
c       correctly convert double precision to single precision
c-----
        real*8 dp(mmaxot)
        real*4 sp(mmaxot)
            do 610 i=1,mmaxot
                if(dabs(dp(i)).lt.1.0d-36)then
                    sp(i) = 0.0
                else
                    sp(i) = sngl(dp(i))
                endif
  610       continue
        return
        end

!         subroutine sprayl(om,c,mmax,csph,usph,ugr)
! c-----
! c       Transform spherical earth to flat earth
! c       and relate the corresponding flat earth dispersion to spherical
! c-----
!         implicit none
! c-----
! c       procedure arguments
! c-----
!         real*8 om, c, csph,usph,ugr
!         integer mmax
! c-----
! c       common blocks
! c-----
!         integer NL
!         parameter(NL=200)
!         common/eigfun/ ur(NL),uz(NL),tz(NL),tr(NL),uu0(4),
!      *                 dcda(NL),dcdb(NL),dcdr(NL),dcdh(NL)
!         real*8 ur, uz, tz, tr, uu0
!         real*8 dcda, dcdb, dcdr, dcdh
!         common/sphere2/vtp(NL),dtp(NL),rtp(NL)
!         real*4 vtp,dtp,rtp
! c-----
! c       local arguments
! c-----
!         real*8 ar, tm
!         integer i
! 
!         ar = 6370.0d0
!         tm=sqrt(1.+(c/(2.*ar*om))**2)
!             do 20 i=1,mmax
!                 dcda(i)=dcda(i)*  vtp(i)/(tm**3)
!                 dcdb(i)=dcdb(i)*  vtp(i)/(tm**3)
!                 dcdh(i)=dcdh(i)*  dtp(i)/(tm**3)
!                 dcdr(i)=dcdr(i)*  rtp(i)/(tm**3)
!    20       continue
! c       write(6,*)'c flat=',c,' csph=',c/tm
!         usph = ugr*tm
!         csph = c/tm
!         return
!         end

!         subroutine bldsph()
! c-----
! c       Transform spherical earth to flat earth
! c-----
!         implicit none
!         integer NL
!         parameter (NL=200)
! c-----
! c       common blocks
! c-----
!         common/sphere2/vtp(NL),dtp(NL),rtp(NL)
!         real*4 vtp,dtp,rtp
!         common/dmodl/  zd(NL),za(NL),zb(NL),zrho(NL),zqai(NL),zqbi(NL),
!      1      zetap(NL),zetas(NL),zfrefp(NL),zfrefs(NL),
!      2      xmu(NL), xlam(NL), mmax
!         real*8 zd, za, zb, zrho, zqai, zqbi, zetap, zetas, 
!      1      zfrefp, zfrefs,
!      1      xmu, xlam
!         integer mmax
! c-----
! c       local arguments
! c-----
!         real*8 ar, dr, r0, r1, z0, z1
!         real*8 tmp
!         integer i
! c-----
! c       vtp is the factor used to convert spherical 
! c          velocity to flat velocity
! c       rtp is the factor used to convert spherical 
! c          density  to flat density 
! c       dtp is the factor used to convert spherical 
! c          boundary to flat boundary
! c-----
! c-----
! c       duplicate computations of srwvds(IV)
! c-----
!         ar=6370.0d0
!         dr=0.0d0
!         r0=ar
!         z0 = 0.0d+00
!         zd(mmax)=1.0
!         do 10 i=1,mmax
!             r1 = r0 * dexp(-zd(i)/ar)
!             if(i.lt.mmax)then
!                 z1 = z0 + zd(i)
!             else
!                 z1 = z0 + 1.0d+00
!             endif
!             TMP=(ar+ar)/(r0+r1)
!             vtp(i) = TMP
!             rtp(i) = TMP**(-2.275)
!             dtp(i) = sngl(ar/r0)
!             r0 = r1
!             z0 = z1
!    10   continue
! C        write(6,*)'vtp:',(vtp(i),i=1,mmax)
! C        write(6,*)'rtp:',(rtp(i),i=1,mmax)
! C        write(6,*)'dtp:',(dtp(i),i=1,mmax)
! c-----
! c       at this point the model information is no longer used and
! c       will be overwritten
! c-----
!         return
!         end

        function ffunc(nub,dm)
        implicit none
        complex*16 ffunc
        complex*16 nub
        real*8 dm
        complex*16 exqq
        complex*16 argcd
c-----
c       get the f function
c-----
        if(cdabs(nub).lt. 1.0d-08)then
            ffunc = dm
        else
            argcd = nub*dm
            if(dreal(argcd).lt.40.0)then
                  exqq = cdexp(-2.0d+00*argcd)
            else
                  exqq=0.0d+00
            endif
            ffunc = (1.d+00-exqq)/(2.d+00*nub)
        endif
        return
        end

        function gfunc(nub,dm)
        implicit none
        complex*16 gfunc
        complex*16 nub
        real*8 dm
        complex*16 argcd
        argcd = nub*dm
        if(dreal(argcd).lt.75)then
             gfunc = cdexp(-argcd)*dm
        else
             gfunc = dcmplx(0.0d+00, 0.0d+00)
        endif
        return
        end

        function h1func(nua,nub,dm)
        implicit none
        complex*16 h1func
        complex*16 nua, nub
        real*8 dm
        complex*16 argcd
        complex*16 exqq
        if(cdabs(nub+nua).lt. 1.0d-08)then
            h1func = dm
        else
            argcd = (nua+nub)*dm
            if(dreal(argcd).lt.40.0)then
                  exqq = cdexp(-argcd)
            else
                  exqq=0.0d+00
            endif
            h1func = (1.d+00-exqq)/(nub+nua)
        endif
        return
        end

        function h2func(nua,nub,dm)
        implicit none
        complex*16 h2func
        complex*16 nua, nub
        real*8 dm
        complex*16 argcd
        complex*16 exqq, exqp
        if(cdabs(nub-nua).lt. 1.0d-08)then
c-----
c           this should never occur for surface waves
c-----
            h2func =  dm
        else
            argcd = nua*dm
            if(dreal(argcd).lt.40.0)then
                  exqp = cdexp(-argcd)
            else
                  exqp=0.0d+00
            endif
            argcd = nub*dm
            if(dreal(argcd).lt.40.0)then
                  exqq = cdexp(-argcd)
            else
                  exqq=0.0d+00
            endif
            h2func = (exqq - exqp)/(nua-nub)
        endif
        return
        end
        
c-----
c      this section of code is specific to the isotropic problem
c      the TI code will use the same subroutine calls. The
c      conversion of isotropic to TI involves replacement of
c      subroutines and/or pieces of code, especially the common
c      block describing the model parameters
c-----

      subroutine svfunc(omega,wvno)
c
c     This combines the Haskell vector from sub down and
c     Dunkin vector from sub up to form the eigenfunctions.
c
      implicit none
c-----
c     command arguments
c-----
        real*8 omega, wvno
c-----
c     common blocks
c-----
        integer NL
        parameter (NL=200)
        common/dmodl/  zd(NL),za(NL),zb(NL),zrho(NL),zqai(NL),zqbi(NL),
     1    zetap(NL),zetas(NL),zfrefp(NL),zfrefs(NL),
     2    xmu(NL), xlam(NL), mmax
        real*8 zd, za, zb, zrho, zqai, zqbi, zetap, zetas, 
     1      zfrefp, zfrefs,
     1      xmu, xlam
        integer mmax
        common/eigfun/ ur(NL),uz(NL),tz(NL),tr(NL),uu0(4),
     1                 dcda(NL),dcdb(NL),dcdr(NL),dcdh(NL)
        real*8 ur, uz, tz, tr, uu0
        real*8 dcda, dcdb, dcdr, dcdh
        common/dunk/   cd(NL,5),exe(NL),exa(NL)
        real*8 cd
        real*8 exe, exa
        common/wateri/iwat(NL)
        integer iwat
        common/hask/   vv(NL,4)
        real*8 vv
c-----
c       model characterization
c-----
        common/modspec/allfluid
        logical allfluid

c-----
c       unknown common check for clean up
c-----
        common/hwat/wh(NL,2),hex(NL)
        real*8 wh, hex
c-----
c       internal variables
c-----
        real*8 fr
        integer i
        real*8 uu1, uu2, uu3, uu4, ext, f1213, fact
        real*8 cd1, cd2, cd3, cd4, cd5, cd6
        real*8 tz1, tz2, tz3, tz4
        integer jwat
c-----
c       get compound matrix from bottom to top
c-----
        call up(omega,wvno,fr)
        call down(omega,wvno)

c-----
c       get propagator from top to bottom 
c-----
        f1213 = -cd(1,2)
        ur(1) = cd(1,3)/cd(1,2)
        uz(1) = 1.0d+00
        tz(1) = 0.0d+00
        tr(1) = 0.0d+00
        uu0(1) = ur(1)
        uu0(2) = 1.0
        uu0(3) = fr
        uu0(4) = fr
c------
        ext = 0.0
        do 200 i=2,mmax
         cd1=  cd(i,1)
         cd2=  cd(i,2)
         cd3=  cd(i,3)
         cd4= -cd(i,3)
         cd5=  cd(i,4)
         cd6=  cd(i,5)
         tz1 = -vv(i,4)
         tz2 = -vv(i,3)
         tz3 =  vv(i,2)
         tz4 =  vv(i,1)


            uu1 =
     1                  tz2*cd6 - tz3*cd5 + tz4*cd4
            uu2 =
     1        -tz1*cd6          + tz3*cd3 - tz4*cd2
            uu3 =
     1         tz1*cd5 - tz2*cd3          + tz4*cd1
            uu4 =
     1        -tz1*cd4 + tz2*cd2 - tz3*cd1

            ext=exa(i)+exe(i) -exe(1)
            if(ext.gt.-80.0 .and. ext.lt.80.0 ) then
                fact=dexp(ext)

                ur(i)=uu1*fact/f1213
                uz(i)=uu2*fact/f1213
                tz(i)=uu3*fact/f1213
                tr(i)=uu4*fact/f1213
            else
                ur(i) = 0.0
                uz(i) = 0.0
                tz(i) = 0.0
                tr(i) = 0.0
            endif
  200   continue
c-----
c       correction for fluid layers on top if not
c       all fluid
c-----
        if(.not. allfluid)then
                jwat = 0
                do 300 i=1,mmax
                    if(iwat(i).gt.0)then
                            jwat = i
                    else
                            go to 301
                    endif
  300   continue
  301   continue
                if(jwat.gt.0)then
                        do i=1,jwat
                            ur(i) = 0.0
                            tr(i) = 0.0
                        enddo
                endif
        endif       
        return
        end

        subroutine up(omega,wvno,fr)
c-----
c       This finds the values of the Dunkin vectors at
c       each layer boundaries from bottom layer upward.
c
c       Note that these are the reduced vectors, e.g.,
c       The true compound (G An-1 ... A1 H) is
c       True [11 12 13 14 15 16 ] = Reduced [ 11 12 13 -13 14 15 ]
c-----
        implicit none
c-----
c       command line arguments
c-----
        real*8 omega, wvno, fr
c-----
c       common blocks
c-----
        integer NL
        parameter (NL=200)
        common/dmodl/  zd(NL),za(NL),zb(NL),zrho(NL),zqai(NL),zqbi(NL),
     1      zetap(NL),zetas(NL),zfrefp(NL),zfrefs(NL),
     2      xmu(NL), xlam(NL), mmax
        real*8 zd, za, zb, zrho, zqai, zqbi, zetap, zetas, 
     1      zfrefp, zfrefs,
     1      xmu, xlam
        integer mmax
        common/eigfun/ ur(NL),uz(NL),tz(NL),tr(NL),uu0(4),
     *                 dcda(NL),dcdb(NL),dcdr(NL),dcdh(NL)
        real*8 ur, uz, tz, tr, uu0
        real*8 dcda, dcdb, dcdr, dcdh
        common/dunk/   cd(NL,5),exe(NL),exa(NL)
        real*8 cd
        real*8 exe, exa
        common/wateri/iwat(NL)
        integer iwat
c-----
c       model characterization
c-----
        common/modspec/allfluid
        logical allfluid
c-----
c       unknown common check for clean up
c-----
        common/save/   dd(5,5),aa(4,4),ex1,ex2
        real*8 dd, aa, ex1, ex2
        common/aamatx/ ww(NL),xx(NL),yy(NL),zz(NL),
     1      cospp(NL),cosqq(NL)
        real*8 ww, xx, yy, zz, cospp, cosqq
        common/engerw/ wra,wd,wba
        real*8 wra,wd,wba
c-----
c       internal arguments
c-----

c-----
c       The save labeled common is used to pass the
c       Dunkin 5x5 and Haskell 4x4 matrices to the main program
c       exp(ex1) is the scaling for the Dunkin-Thrower compound matrix
c       exp(ex2) is the scaling for the Haskell matrices
c-----

        complex*16 rp, rsv, p, q
        real*8 wvno2, om2
        complex*16 gbr(2,5)
        real*8 cr, ee(5), exn
        real*8 ca(5,5)
        real*8 xka, xkb, pex, svex

        real*8 cosp,rsinp,sinpr,cossv,rsinsv,sinsvr
        real*8 exsum

        integer i,j,m,mmm1,nmat
c-----
c       initialize base vector
c-----
c-----
c       set up starting values for bottom halfspace
c-----
        wvno2=wvno*wvno
        om2 = omega*omega
        call evalg2(0,mmax,mmax-1,gbr,1,
     1      wvno,omega,om2,wvno2)
c-----
c       note for surface waves, the ra, rb are real for the halfspace as long
c       as the phase velocity is less than the respective halfspace velocity
c-----

            cd(mmax,1)= dreal(gbr(1,1))
            cd(mmax,2)= dreal(gbr(1,2))
            cd(mmax,3)= dreal(gbr(1,3))
            cd(mmax,4)= dreal(gbr(1,4))
            cd(mmax,5)= dreal(gbr(1,5))
            exe(mmax) = 0.0d+00
c------
c       matrix multiplication from bottom layer upward
c------
        mmm1=mmax-1
        exsum = 0.0
        do 500 m = mmm1,1,-1
                xka = omega/za(m)
                if(zb(m).gt.0.0)then
                  xkb = omega/zb(m)
                else
                  xkb = 0.0
                endif
                rp =CDSQRT(dcmplx(wvno2-xka*xka,0.0d+00))
                rsv=CDSQRT(dcmplx(wvno2-xkb*xkb,0.0d+00))
                p = rp  * zd(m)
                q = rsv * zd(m)
                call varsv(p,q,rp, rsv,
     1              cosp, cossv, rsinp, rsinsv,
     1              sinpr, sinsvr, pex,svex,iwat(m))
                call dnka2(CA,cosp,rsinp,sinpr,cossv,rsinsv,sinsvr,
     1              sngl(zrho(m)),sngl(zb(m)),iwat(m),pex,pex+svex,
     2              wvno,wvno2,om2)

            nmat = 5
            do 200 i=1,nmat
                cr=0.0d+00
                do 100 j=1,nmat
                    cr=cr+cd(m+1,j)*ca(j,i)
  100           continue
                ee(i)=cr
  200       continue
            exn= 0.0d+00
            call normc(ee,exn,nmat)
            exsum = exsum + pex + svex + exn
            exe(m) = exsum
            do 300 i = 1,nmat
                cd(m,i)=ee(i)
  300       continue
  500   continue
c-----
c       define period equation
c-----
        fr=cd(1,1)
        return
        end

        subroutine dnka2(CA,cosp,rsinp,sinpr,cossv,rsinsv,sinsvr,
     1              rho,b,iwat,ex,exa,wvno,wvno2,om2)
        implicit none
        real*8 ca(5,5), wvno, wvno2, om2
        real*4 rho,b
        real*8 cosp,rsinp,sinpr,cossv,rsinsv,sinsvr
        integer iwat

        real rho2
        real*8 gam
        real*8 ex, exa
        real*8 a0
        real*8 cpcq,cpy,cpz,cqw,cqx,xy,xz,wy,wz

        real*8 gam2,gamm1,gamm2,a0c,xz2,wy2,temp
        real*8 dfac
        real*8 cqww2, cqxw2, g1wy2, gxz2, g2wy2, g2xz2
        real*8 gg1, a0cgg1
        integer i, j

        if(iwat.eq.1)then
c-----
c       fluid layer
c-----
            do 100 j=1,5
                do 101 i=1,5
                    ca(i,j) = 0.0d+00
  101           continue
  100       continue
            if(ex.gt.35.0d+00)then
                dfac = 0.0d+00
            else
                dfac = dexp(-ex)
            endif
            ca(3,3) = dfac
            ca(1,1) = cosp
            ca(5,5) = cosp
            ca(1,2) = - rsinp/(rho*om2)
            ca(2,1) = - rho*sinpr*om2
            ca(2,2) = cosp
            ca(4,4) = cosp
            ca(4,5) = ca(1,2)
            ca(5,4) = ca(2,1)
        else
            if ( exa.lt. 60.0)then
                a0=dexp(-exa)
            else
                a0 = 0.0d+00
            endif
            cpcq = cosp * cossv
            cpy  = cosp*sinsvr
            cpz  = cosp*rsinsv
            cqw  = cossv*sinpr
            cqx  = cossv*rsinp
            xy   = rsinp*sinsvr
            xz   = rsinp*rsinsv
            wy   = sinpr*sinsvr  
            wz   = sinpr*rsinsv
c-----
c       elastic layer
c-----
            rho2= rho*rho
            gam = 2.0*b*b*wvno2/om2
            gam2  = gam*gam
            gamm1 = gam-1.
            gamm2 = gamm1*gamm1
            cqww2 = cqw * wvno2
            cqxw2 = cqx / wvno2
            gg1 = gam*gamm1
            a0c  = dcmplx(2.0d+00,0.0d+00)*
     1          (dcmplx(a0,0.0d+00)-cpcq)
            xz2  = xz/wvno2
            gxz2 = gam*xz2
            g2xz2 = gam2 * xz2
            a0cgg1 = a0c*(gam+gamm1)
            wy2  = wy*wvno2
            g2wy2 = gamm2 * wy2
            g1wy2 = gamm1 * wy2

c-----
c       OK by symmetry
c----
            temp = a0c*gg1 + g2xz2 + g2wy2
            ca(3,3) = a0 + temp + temp
            ca(1,1) = cpcq-temp
            ca(1,2) = (-cqx + wvno2*cpy)/(rho*om2)
            temp = dcmplx(0.5d+00,0.0d+00)*a0cgg1 + gxz2 + g1wy2
            ca(1,3) = wvno*temp/(rho*om2)

            ca(1,4) = (-cqww2+cpz)/(rho*om2)
            temp = wvno2*(a0c + wy2) + xz
            ca(1,5) = -temp/(rho2*om2*om2)

            ca(2,1) = (-gamm2*cqw + gam2*cpz/wvno2)*rho*om2
            ca(2,2) = cpcq
            ca(2,3) = (gamm1*cqww2 - gam*cpz)/wvno
            ca(2,4) = -wz
            ca(2,5)=ca(1,4)


            temp =dcmplx(0.5d+00,0.0d+00)*a0cgg1*gg1 
     1          + gam2*gxz2 + gamm2*g1wy2
            ca(3,1) = -dcmplx(2.0d+00,0.0d+00)*temp*rho*om2/wvno
            ca(3,2) = -wvno*(gam*cqxw2 - gamm1*cpy)*
     1          dcmplx(2.0d+00,0.0d+00)

            ca(3,4)=-2.0d+00*ca(2,3)
            ca(3,5)=-2.0d+00*ca(1,3)

            ca(4,1) = (-gam2*cqxw2 + gamm2*cpy)*rho*om2
            ca(4,2) = -xy
            ca(4,3)= -ca(3,2)/2.0d+00
            ca(4,4)=ca(2,2)
            ca(4,5)=ca(1,2)

            temp = gamm2*(a0c*gam2 + g2wy2) + gam2*g2xz2
            ca(5,1) = -rho2*om2*om2*temp/wvno2
            ca(5,2)=ca(4,1)
            ca(5,3)=-ca(3,1)/2.0d+00
            ca(5,4)=ca(2,1)
            ca(5,5)=ca(1,1)
        endif
        return
        end

        subroutine evalg2(jbdry,m,m1,gbr,inp,
     1      wvno,om,om2,wvno2)
c-----
c       this is from hspec96, but not everything is required
c       tot he layered halfspace prroblem
c-----
        implicit none
c-----
c       procedure arguments
c-----
        integer jbdry, m, m1, inp
        complex*16 gbr(2,5)
        real*8 wvno, wvno2, om, om2
c-----
c       common blocks
c-----
        integer NL
        parameter(NL=200)
        common/dmodl/  zd(NL),za(NL),zb(NL),zrho(NL),zqai(NL),zqbi(NL),
     1    zetap(NL),zetas(NL),zfrefp(NL),zfrefs(NL),
     2    xmu(NL), xlam(NL), mmax
        real*8 zd, za, zb, zrho, zqai, zqbi, zetap, zetas,
     1      zfrefp, zfrefs,
     1      xmu, xlam
        integer mmax
        common/modspec/allfluid
        logical allfluid
        common/emat/e,einv,ra,rb
        complex*16 ra,rb
        complex*16 e(4,4), einv(4,4)
        common/wateri/iwat(NL)
        integer iwat

c-----
c       internal parameters
c-----
        real*8 xka,xkb,gam,gamm1
        complex*16 CDSQRT


        integer i,j
        
c-----
c       set up halfspace conditions
c-----
        xka = om/za(m)
        if(zb(m).gt.0.0)then
          xkb = om/zb(m)
        else
          xkb = 0.0
        endif
        ra=CDSQRT(dcmplx(wvno2-xka*xka,0.0d+00))
        rb=CDSQRT(dcmplx(wvno2-xkb*xkb,0.0d+00))
        gam = dble(zb(m))*wvno/om
        gam = 2.0d+00 * (gam * gam)
        gamm1 = gam - dcmplx(1.0d+00,0.0d+00)
c-----
c       set up halfspace boundary conditions
c
c       jbdry   = -1  RIGID
c           =  0  ELASTIC
c           = +1  FREE SURFACE
c
c-----
        if(jbdry.lt.0)then
c-----
c       RIGID - check properties of layer above
c-----
            if(zb(m) .gt. 0.0)then
c-----
c               ELASTIC ABOVE - RIGID
c-----
                gbr(inp,1) = dcmplx(1.0d+00,0.0d+00)
                gbr(inp,2) = dcmplx(0.0d+00,0.0d+00)
                gbr(inp,3) = dcmplx(0.0d+00,0.0d+00)
                gbr(inp,4) = dcmplx(0.0d+00,0.0d+00)
                gbr(inp,5) = dcmplx(0.0d+00,0.0d+00)
            else
c-----
c               FLUID ABOVE - RIGID
c-----
                gbr(inp,1) = dcmplx(0.0d+00,0.0d+00)
                gbr(inp,2) = dcmplx(0.0d+00,0.0d+00)
                gbr(inp,3) = dcmplx(0.0d+00,0.0d+00)
                gbr(inp,4) = dcmplx(0.0d+00,0.0d+00)
                gbr(inp,5) = dcmplx(0.0d+00,0.0d+00)
                if(allfluid)then
                    gbr(inp,1) = dcmplx(1.0d+00,0.0d+00)
                else
                    gbr(inp,4) = dcmplx(1.0d+00,0.0d+00)
                endif
c-----
c               (pseudo SH)
c-----
            endif
        else if(jbdry.eq.0)then
c-----
c       HALFSPACE
c-----
            if(iwat(m).eq.0)then
c-----
c               ELASTIC HALFSPACE
c-----
c       multiply G of Herrmann 2001 by - rho^2 om^4 k^2 ra rb
c       should have no effect since it is in both numerator and
c       denominator -- however will not give the correct potential
c       coefficient -- so rethink?
c-----
            E(1,1)= wvno
            E(1,2)= rb
            E(1,3)= wvno
            E(1,4)= -rb

            E(2,1)= ra
            E(2,2)= wvno
            E(2,3)= -ra
            E(2,4)= wvno

            E(3,1)= zrho(m)*om2*gamm1
            E(3,2)= zrho(m)*om2*gam*rb/wvno
            E(3,3)= zrho(m)*om2*gamm1
            E(3,4)= -zrho(m)*om2*gam*rb/wvno

            E(4,1)= zrho(m)*om2*gam*ra/wvno
            E(4,2)= zrho(m)*om2*gamm1
            E(4,3)= -zrho(m)*om2*gam*ra/wvno
            E(4,4)= zrho(m)*om2*gamm1

            EINV(1,1)= 0.5*gam/wvno
            EINV(1,2)= -0.5*gamm1/ra
            EINV(1,3)= -0.5/(zrho(m)*om2)
            EINV(1,4)= 0.5*wvno/(zrho(m)*om2*ra)

            EINV(2,1)= -0.5*gamm1/rb
            EINV(2,2)= 0.5*gam/wvno
            EINV(2,3)= 0.5*wvno/(zrho(m)*om2*rb)
            EINV(2,4)= -0.5/(zrho(m)*om2)

            EINV(3,1)= 0.5*gam/wvno
            EINV(3,2)=  0.5*gamm1/ra
            EINV(3,3)= -0.5/(zrho(m)*om2)
            EINV(3,4)= -0.5*wvno/(zrho(m)*om2*ra)

            EINV(4,1)= 0.5*gamm1/rb
            EINV(4,2)= 0.5*gam/wvno
            EINV(4,3)= -0.5*wvno/(zrho(m)*om2*rb)
            EINV(4,4)= -0.5/(zrho(m)*om2)

                gbr(inp,1)=dble(zrho(m)*zrho(m))*om2*om2*
     1              (-gam*gam*ra*rb+wvno2*gamm1*gamm1)
                gbr(inp,2)=-dble(zrho(m))*(wvno2*ra)*om2
                gbr(inp,3)=-dble(zrho(m))*(-gam*ra*rb+wvno2*gamm1)
     1              *om2*wvno
                gbr(inp,4)=dble(zrho(m))*(wvno2*rb)*om2
                gbr(inp,5)=wvno2*(wvno2-ra*rb)
             gbr(inp,1)=0.25*gbr(inp,1)/
     1               (-zrho(m)*zrho(m)*om2*om2*wvno2*ra*rb)
             gbr(inp,2)=0.25*gbr(inp,2)/
     1               (-zrho(m)*zrho(m)*om2*om2*wvno2*ra*rb)
             gbr(inp,3)=0.25*gbr(inp,3)/
     1               (-zrho(m)*zrho(m)*om2*om2*wvno2*ra*rb)
             gbr(inp,4)=0.25*gbr(inp,4)/
     1               (-zrho(m)*zrho(m)*om2*om2*wvno2*ra*rb)
             gbr(inp,5)=0.25*gbr(inp,5)/
     1               (-zrho(m)*zrho(m)*om2*om2*wvno2*ra*rb)

            else if(iwat(m).eq.1)then
c-----
c               FLUID HALFSPACE
c-----
                if(allfluid)then
                    gbr(inp,1) = dble(0.5) / ra
                    gbr(inp,2) = dcmplx(0.5d+00,0.0d+00)/
     1                  (-dble(zrho(m))*om2)
                    gbr(inp,3) = dcmplx(0.0d+00,0.0d+00)
                    gbr(inp,4) = dcmplx(0.0d+00,0.0d+00)
                    gbr(inp,5) = dcmplx(0.0d+00,0.0d+00)
                else
                    gbr(inp,1) = dcmplx(0.0d+00,0.0d+00)
                    gbr(inp,2) = dcmplx(0.0d+00,0.0d+00)
                    gbr(inp,3) = dcmplx(0.0d+00,0.0d+00)
                    gbr(inp,4) = dble(0.5*zrho(m)*om2) / ra
                    gbr(inp,5) = dcmplx(-0.5d+00,0.0d+00)
                endif
c-----
c           for safety null the matrices and then fill with a 2x2
c-----
            do i=1,4
               do j=1,4
                  E(i,j) = dcmplx(0.0d+00, 0.0d+00)
                  EINV(i,j) = dcmplx(0.0d+00, 0.0d+00)
               enddo
            enddo
            E(1,1)=  ra
            E(1,2)= -ra
            E(2,1)= -zrho(m)*om2
            E(2,2)= -zrho(m)*om2

            EINV(1,1)= 0.5/ra
            EINV(1,2)= -0.5/(zrho(m)*om2)
            EINV(2,1)= -0.5/ra
            EINV(2,2)= -0.5/(zrho(m)*om2)
            endif
        else if(jbdry.eq.1)then
c-----
c       FREE - check properties of layer above
c-----
            if(zb(m) .gt. 0.0)then
                gbr(inp,1) = dcmplx(0.0d+00,0.0d+00)
                gbr(inp,2) = dcmplx(0.0d+00,0.0d+00)
                gbr(inp,3) = dcmplx(0.0d+00,0.0d+00)
                gbr(inp,4) = dcmplx(0.0d+00,0.0d+00)
                gbr(inp,5) = dcmplx(1.0d+00,0.0d+00)
                
            else
                gbr(inp,1) = dcmplx(0.0d+00,0.0d+00)
                gbr(inp,2) = dcmplx(0.0d+00,0.0d+00)
                gbr(inp,3) = dcmplx(0.0d+00,0.0d+00)
                gbr(inp,4) = dcmplx(0.0d+00,0.0d+00)
                gbr(inp,5) = dcmplx(0.0d+00,0.0d+00)
                if(allfluid)then
                    gbr(inp,2) = dcmplx(1.0d+00,0.0d+00)
                else
                    gbr(inp,5) = dcmplx(1.0d+00,0.0d+00)
                endif
            endif
        endif
        return
        end

        subroutine hska(AA,cosp,rsinp,sinpr,tcossv,trsinsv,tsinsvr,
     2      rho,b,iwat,pex,svex,wvno,wvno2,om2)
c-----
c       Changes
c
c-----
        implicit none
c-----
c       subroutine variables
c-----
        real*8 AA(4,4)
        real*8 cosp , tcossv 
        real*8 rsinp, trsinsv
        real*8 sinpr, tsinsvr
        real rho,b
        integer iwat
        real*8 pex, svex
        real*8  om2, wvno, wvno2

c-----
c       local variables
c-----
        real*8 gam, gamm1
        real*8 cossv 
        real*8 rsinsv
        real*8 sinsvr
        real*8 dfac

        integer i, j

        if(iwat.eq.1)then
c-----
c       fluid layer
c-----
            do 100 j=1,4
                do 101 i=1,4
                    AA(i,j) = 0.0d+00
  101           continue
  100       continue
            if(pex.gt.35.0d+00)then
                dfac = 0.0d+00
            else
                dfac = dexp(-pex)
            endif
            AA(1,1) = dfac
            AA(4,4) = dfac
            AA(2,2) = cosp
            AA(3,3) = cosp
            AA(2,3) = -rsinp/(rho*om2)
            AA(3,2) = - rho*om2*sinpr
        else
c-----
c       elastic layer
c-----
            if( (pex-svex) .gt. 70.0)then
                dfac = 0.0d+00
            else
                dfac = dexp(svex-pex)
            endif
            cossv = dfac * tcossv
            rsinsv = dfac * trsinsv
            sinsvr = dfac * tsinsvr
                
            gam = 2.0*b*b*wvno2/om2
            gamm1 = gam -1.0
C            AA(1,1) =  gam*cosp - gamm1*cossv
            AA(1,1) =  cossv + gam*(cosp - cossv)
            AA(1,2) =  -wvno*gamm1*sinpr + gam*rsinsv/wvno
            AA(1,3) =  -wvno*(cosp-cossv)/(rho*om2)
            AA(1,4) =   (wvno2*sinpr - rsinsv)/(rho*om2)
            AA(2,1) =   gam*rsinp/wvno - wvno*gamm1*sinsvr
            AA(2,2) =   cosp - gam*(cosp- cossv)
            AA(2,3) =   ( -rsinp + wvno2*sinsvr)/(rho*om2)
            AA(2,4) = - AA(1,3)
            AA(3,1) =   rho*om2*gam*gamm1*(cosp-cossv)/wvno
            AA(3,2) = rho*om2*(-gamm1*gamm1*sinpr+gam*gam*rsinsv/wvno2)
            AA(3,3) =   AA(2,2)
            AA(3,4) = - AA(1,2)
            AA(4,1) =   rho*om2*(gam*gam*rsinp/wvno2-gamm1*gamm1*sinsvr)
            AA(4,2) = - AA(3,1)
            AA(4,3) = - AA(2,1)
            AA(4,4) =   AA(1,1)
        endif
        return
        end

        subroutine down(omega,wvno)
c-----
c       This finds the values of the Haskell vectors at
c       each layer boundaries from top layer downward.
c-----
        implicit none
c-----
c     command arguments
c-----
        real*8 omega, wvno
c-----
c     common blocks
c-----
        integer NL
        parameter (NL=200)
        common/dmodl/  zd(NL),za(NL),zb(NL),zrho(NL),zqai(NL),zqbi(NL),
     1    zetap(NL),zetas(NL),zfrefp(NL),zfrefs(NL),
     2    xmu(NL), xlam(NL), mmax
        real*8 zd, za, zb, zrho, zqai, zqbi, zetap, zetas,
     1      zfrefp, zfrefs,
     1      xmu, xlam
        integer mmax
        common/eigfun/ ur(NL),uz(NL),tz(NL),tr(NL),uu0(4),
     1                 dcda(NL),dcdb(NL),dcdr(NL),dcdh(NL)
        real*8 ur, uz, tz, tr, uu0
        real*8 dcda, dcdb, dcdr, dcdh
        common/dunk/   cd(NL,5),exe(NL),exa(NL)
        real*8 cd
        real*8 exe, exa
        common/wateri/iwat(NL)
        integer iwat
        common/hask/   vv(NL,4)
        real*8 vv
c-----
c       model characterization
c-----
        common/modspec/allfluid
        logical allfluid
c-----
c      internal variables
c-----
       real*8 wvno2, om2
       integer m
       real*8 aa(4,4)
       integer i,j
       real*8 xka, xkb, pex, svex
       real*8 ex2
       complex*16 rp, rsv, p, q
       real*8 cosp,rsinp,sinpr,cossv,rsinsv,sinsvr
       real*8 cc, aa0(4)
       real*8 exsum

       om2 = omega*omega
       wvno2 = wvno*wvno
c-----
c      propagate down to get the first column of the
c      Am .. A2 A1
c-----
c-----
c      initialize the top surface for the
c      first column of the Haskell propagator
c-----
      do i=1,4
         if(i.eq.1)then
             vv(1,i) = 1.0d+00
         else
             vv(1,i) = 0.0d+00
         endif
      enddo
      exa(1) = 0.0
      
c------
c       matrix multiplication from top layer downward
c------
       exsum  = 0.0
       do 500 m= 1, mmax -1
                xka = omega/za(m)
                if(zb(m).gt.0.0)then
                  xkb = omega/zb(m)
                else
                  xkb = 0.0
                endif
                rp =CDSQRT(dcmplx(wvno2-xka*xka,0.0d+00))
                rsv=CDSQRT(dcmplx(wvno2-xkb*xkb,0.0d+00))
                p = rp  * zd(m)
                q = rsv * zd(m)
                call varsv(p,q,rp, rsv,
     1              cosp, cossv, rsinp, rsinsv,
     1              sinpr, sinsvr, pex,svex,iwat(m))

                call hska(AA,cosp,rsinp,sinpr,
     1                cossv,rsinsv,sinsvr,
     2                sngl(zrho(m)),sngl(zb(m)),iwat(m),
     3                pex,svex,wvno,wvno2,om2)
            do 300 i=1,4
                cc=0.0d+00
                do 200 j=1,4
                    cc=cc+aa(i,j)*vv(m,j)
  200           continue
                aa0(i)=cc
  300       continue
            ex2 = 0.0
            call normc(aa0,ex2,4)
            exsum = exsum + pex + ex2
            exa(m+1)= exsum
            
            do 400 i=1,4
                vv(m+1,i)=aa0(i)
  400       continue
  500   continue
c-----
c       vv is  column 1 of the Haskell propagator at the top of layer m
c-----
        return
        end


        subroutine gammap(omega,wvno,gammar)
c-----
c       This routine finds the attenuation gamma value.
c
c-----
        implicit none
c-----
c       routine arguments
c-----
        real*8 omega, wvno, gammar
c-----
c       common blocks
c-----
        integer NL
        parameter (NL=200)
        common/dmodl/  zd(NL),za(NL),zb(NL),zrho(NL),zqai(NL),zqbi(NL),
     1      zetap(NL),zetas(NL),zfrefp(NL),zfrefs(NL),
     2      xmu(NL), xlam(NL), mmax
        real*8 zd, za, zb, zrho, zqai, zqbi, zetap, zetas, 
     1      zfrefp, zfrefs,
     1      xmu, xlam
        integer mmax
        common/wateri/iwat(NL)
        integer iwat
        common/eigfun/ ur(NL),uz(NL),tz(NL),tr(NL),uu0(4),
     1                 dcda(NL),dcdb(NL),dcdr(NL),dcdh(NL)
        real*8 ur, uz, tz, tr, uu0
        real*8 dcda, dcdb, dcdr, dcdh
c-----
c       internal variables
c-----
        real*8 dc, pi, x, omgref, c
        integer i

        gammar=0.0
        dc = 0.0
        pi = 3.141592653589493d+00
        do 100 i=1,mmax
            if(iwat(i).eq.  0)then
                x=dcdb(i)*zb(i)*zqbi(i)
                gammar = gammar + x
                omgref=2.0*pi*zfrefs(i)
                dc = dc + dlog(omega/omgref)*x/pi
            endif
            x=dcda(i)*za(i)*zqai(i)
            gammar = gammar + x
            omgref=2.0*pi*zfrefp(i)
            dc = dc + dlog(omega/omgref)*x/pi
  100   continue
        c=omega/wvno
        gammar=0.5*wvno*gammar/c
        c=c+dc
        wvno=omega/c
        return
        end

        subroutine energy(om,wvno,mmax)
c-----
c       determine the energy integrals and also the
c       phase velocity partials
c-----
        implicit none
c-----
c       subroutine arguments
c-----
        real*8 om, wvno
        integer mmax
c-----
c       common blocks
c-----
        integer NL
        parameter (NL=200)
        common/sumi/   sumi0,sumi1,sumi2,sumi3,flagr,are,ugr
        real*8 sumi0, sumi1, sumi2, sumi3, flagr, are, ugr
        common/eigfun/ ur(NL),uz(NL),tz(NL),tr(NL),uu0(4),
     1                 dcda(NL),dcdb(NL),dcdr(NL),dcdh(NL)
        real*8 ur, uz, tz, tr, uu0
        real*8 dcda, dcdb, dcdr, dcdh
        common/wateri/iwat(NL)
        integer iwat
        
c-----
c       external function call prototypes
c-----
        real*8 intijr
c-----
c       internal variables
c-----
        real*8 om2, wvno2
        real*8 TA, TC, TF, TL, TN
        real*8 INT11, INT13, INT22, INT44, INT33, INT24
        real*8 URUR, UZUZ, DURDUR, DUZDUZ, URDUZ, UZDUR
        real*8 c
        real*8 fac
        real*8 facah, facav, facbh, facbv,  facr
c       real*8 face

        integer m
c-----
c       coefficients of the ODE
c-----
        real*8 a12, a14, a21, a23
        real *8 ah, av, bh, bv, eta, rho

        integer TYPELYR
c-----
c       TYPELYR = -1  layer represents upper halfspace
c                  0  layer is true intenal layer
c                 +1  layer represents lower halfspace
c-----

c----
c       initialize
c----

        sumi0 = 0.0
        sumi1 = 0.0
        sumi2 = 0.0
        sumi3 = 0.0
        c = om/wvno
        om2 = om*om
        wvno2 = wvno*wvno

        do m=1,mmax
           call getmat(m,wvno,om,a12, a14, a21, a23,
     1      ah,av,bh,bv,eta,rho,
     2      TA,TC,TF,TL,TN,iwat(m))
c------
c       get the integrals for layers over a halfspace
c       this is here is we ever adopt the code to the coal 
c       seam problem
c-----
           if(m.eq.mmax)then
                   typelyr = 1
           else
                   typelyr = 0
           endif
           INT11 = intijr(1,1,m,typelyr,om,om2,wvno,wvno2)
           INT13 = intijr(1,3,m,typelyr,om,om2,wvno,wvno2)
           INT22 = intijr(2,2,m,typelyr,om,om2,wvno,wvno2)
           INT24 = intijr(2,4,m,typelyr,om,om2,wvno,wvno2)
           INT33 = intijr(3,3,m,typelyr,om,om2,wvno,wvno2)
           INT44 = intijr(4,4,m,typelyr,om,om2,wvno,wvno2)


           if(iwat(m).eq.1)then
c-----
c       fluid - not these are for the 4x4 formulation
c-----
             URUR   = INT22*(wvno/(rho*om2))**2
             UZUZ   = INT11
             URDUZ  =  - (wvno/(rho*om2))*a12*INT22
             DUZDUZ = a12*a12*INT22
             sumi0  = sumi0 + rho*(URUR + UZUZ)
             sumi1  = sumi1 + TA*URUR
             sumi2  = sumi2 - TF*URDUZ
             sumi3  = sumi3 + TC*DUZDUZ
             facah = rho*ah*(URUR -2.*eta*URDUZ/wvno)
             facav = rho*av*DUZDUZ / wvno2
             dcda(m) = facah + facav
             facr = -0.5*c*c*(URUR + UZUZ)
             dcdr(m) = 0.5*(av*facav + ah*facah )/rho + facr
c-----
c            define the ur in the fliud from the Tz - this will be at
c            the top of the layer
c-----
             ur(m) = - wvno*tz(m)/(rho*om2)

           else
c-----
c       solid
c-----
             URUR   = INT11
             UZUZ   = INT22
             DURDUR = a12*a12*INT22 + 2.*a12*a14*INT24 + a14*a14*INT44
             DUZDUZ = a21*a21*INT11 + 2.*a21*a23*INT13 + a23*a23*INT33
             URDUZ  = a21*INT11 + a23*INT13
             UZDUR  = a12*INT22 + a14*INT24
             sumi0  = sumi0 + rho*(URUR + UZUZ)
             sumi1  = sumi1 + TL*UZUZ + TA*URUR
             sumi2  = sumi2 + TL*UZDUR - TF*URDUZ
             sumi3  = sumi3 + TL*DURDUR + TC*DUZDUZ

c-----
c            partial derivatives of phase velocity with
c            respect to medium parameters. Note that these are
c            later divided by (U sumi0) when these are finalized
c
c            note that the code distainguishes between
c            ah and av, bh and bv and uses eta. These are
c            actually TI parameters, and for isotropic media
c            ah = av, bh = bv aned eta = 1. The code is written this
c            way for easier conversion to a TI case
c-----
             facah = rho*ah*(URUR -2.*eta*URDUZ/wvno)
             facav = rho*av*DUZDUZ / wvno2
             facbh = 0.0
             facbv = rho*bv*(UZUZ + 2.*UZDUR/wvno + DURDUR/wvno2 +
     1           4.*eta*URDUZ/wvno )
             dcda(m) = facah + facav
             dcdb(m) = facbv + facbh
             facr = -0.5*c*c*(URUR + UZUZ)
c-----
c      this is correct for TI
             dcdr(m) = 0.5*(av*facav + ah*facah + bv*facbv)/rho + facr

c-----
c       partial with layer thickness needs the value at the layer, e.g.,
c       dcdh(1) is top surface
c-----
           endif
        enddo
c-----
c       determine final parameters
c-----

        flagr=om2*sumi0-wvno2*sumi1-2.d+00*wvno*sumi2-sumi3
        ugr=(wvno*sumi1+sumi2)/(om*sumi0)
        are=wvno/(2.d+00*om*ugr*sumi0)
        fac = are*c/wvno2
c-----
c       use the final factors to apply the 1/(U I0) factor
c----
        do m=1,mmax
           dcda(m) = dcda(m) /(ugr*sumi0)
           dcdb(m) = dcdb(m) /(ugr*sumi0)
           dcdr(m) = dcdr(m) /(ugr*sumi0)
        enddo
c-----
c       determine the dcdh
c-----
        call getdcdh(om2,wvno,wvno2,fac)
        return
        end

        subroutine getmat(m,wvno,om,a12, a14, a21, a23,
     1         ah,av,bh,bv,eta,rho,
     2         TA,TC,TF,TL,TN,iwat)
        
c-----
c       get matrix elements of the ODE
c       We do not require all eight non-zero elements
c       also return the model characterization parameters
c-----
        implicit none
c-----
c       procedure arguments
c-----
        real*8 wvno,om,a12, a14, a21, a23
        real*8 ah,av,bh,bv,eta,rho
        real*8 TA,TC,TF,TL,TN
        integer m, iwat
c-----
c       common blocks
c-----

        integer NL
        parameter (NL=200)
        common/dmodl/  zd(NL),za(NL),zb(NL),zrho(NL),zqai(NL),zqbi(NL),
     1      zetap(NL),zetas(NL),zfrefp(NL),zfrefs(NL),
     2      xmu(NL), xlam(NL), mmax
        real*8 zd, za, zb, zrho, zqai, zqbi, zetap, zetas, 
     1      zfrefp, zfrefs,
     1      xmu, xlam
        integer mmax

        if(iwat.eq.1)then
c-----
c            non-gravitating fluid - using 2x2 ODE
c-----
             ah= za(m)
             av= za(m)
             bh= 0.0
             bv= 0.0
             rho=zrho(m)
             eta = 1.0
     
             TL = 0.0
             TN = 0.0
             TC = zrho(m)*za(m)*za(m)
             TA = zrho(m)*za(m)*za(m)
             TF = TA - 2.*TN
     
             a12 = - ( wvno*wvno - om*om/(ah*ah))/(rho*om*om)
        else
c-----
c            elastic - using 4x4 ODE
c-----
             ah= za(m)
             av= za(m)
             bh= zb(m)
             bv= zb(m)
             rho=zrho(m)
             eta = 1.0
     
     
             TL = zrho(m)*zb(m)*zb(m)
             TN = zrho(m)*zb(m)*zb(m)
             TC = zrho(m)*za(m)*za(m)
             TA = zrho(m)*za(m)*za(m)
             TF = TA - 2.*TN
     
             a12 = -wvno
             a14 = 1.0/TL
             a21 = wvno * TF/TC
             a23 = 1.0/TC
        endif

        return
        end

        function intijr(i,j,m,typelyr,om,om2,wvno,wvno2)
        implicit none
c-----
c       procedure arguments
c-----
        real*8 intijr
        integer i,j,m
        real*8 om, om2, wvno,wvno2
        integer TYPELYR
c-----
c       TYPELYR = -1  layer represents upper halfspace
c                  0  layer is true intenal layer
c                 +1  layer represents lower halfspace
c-----
c NOTE DO NOT PERMIT RA RB to be ZERO add a small number TAKEN CARE OF
c IN FUNC CALLS
c beware of E matrix for fluid - internally I use a 4x4
c but hte book uses a 2x2 in the 8-10 problem
c need typelyr, wvno, om, om2, wvno2, m, mmax, do not need medium
c-----
c       Potential coefficients
c       kmpu, kmsu are the upward coefficients at bottom of layer
c       km1pd, km1sd are the downward coefficients at top of layer
c-----
        integer NL
        parameter (NL=200)
        common/dmodl/  zd(NL),za(NL),zb(NL),zrho(NL),zqai(NL),zqbi(NL),
     1      zetap(NL),zetas(NL),zfrefp(NL),zfrefs(NL),
     2      xmu(NL), xlam(NL), mmax
        real*8 zd, za, zb, zrho, zqai, zqbi, zetap, zetas, 
     1      zfrefp, zfrefs,
     1      xmu, xlam
        integer mmax
        common/eigfun/ ur(NL),uz(NL),tz(NL),tr(NL),uu0(4),
     1                 dcda(NL),dcdb(NL),dcdr(NL),dcdh(NL)
        real*8 ur, uz, tz, tr, uu0
        real*8 dcda, dcdb, dcdr, dcdh
        common/wateri/iwat(NL)
        integer iwat
        common/emat/e,einv,ra,rb
        complex*16 e(4,4), einv(4,4)
        complex*16 ra, rb

c-----
c       external function definitions
c-----
        complex*16 ffunc, gfunc, h1func, h2func
c-----
c       internal variables
c-----
        complex*16 cintijr, FA, GA, FB, GB, H1, H2
        complex*16 gbr(2,5)
        complex*16 kmpu, kmsu, km1pd, km1sd


c-----
c       call evalg to get the E and EINV matrices
c       evalg knows about water - we ignore everything else
c-----
        call evalg(0,m,m-1,gbr,1,
     1      wvno,om,om2,wvno2)
c-----
c       for an elastic solid


        if(iwat(m).eq.1)then
c-----
c       fluid
c-----
            if(typelyr .lt. 0)then
              kmpu = einv(1,1)*uz(m+1)
     1             + einv(1,2)*tz(m+1) 
              cintijr = e(i,1)*e(j,1)*kmpu*kmpu/(2.0*ra)
            else if(typelyr .eq. 0)then
              km1pd = einv(2,1)*uz(m)
     1              + einv(2,2)*tz(m) 
              kmpu = einv(1,1)*uz(m+1)
     1             + einv(1,2)*tz(m+1)  
              FA=ffunc(ra,zd(m))
              GA=gfunc(ra,zd(m))
              cintijr = e(i,1)*e(j,1)*kmpu*kmpu*FA
     1          +(e(i,1)*e(j,2)+e(i,2)*e(j,1))*kmpu*km1pd*GA       
     2          + e(i,2)*e(j,2)*km1pd*km1pd*FA
            else
              km1pd = einv(2,1)*uz(m)
     2              + einv(2,2)*tz(m)
              cintijr = e(i,2)*e(j,2)*km1pd*km1pd/(2.0*ra)
            endif
        else
c-----
c       solid
c-----
            if(typelyr .lt. 0)then
c-----
c             potential coefficients for upper halfspace
c             based on the displacement, stress at top
c             Basically m = 1
c-----
              kmpu = einv(1,1)*ur(m) + einv(1,2)*uz(m)
     2             + einv(1,3)*tz(m) + einv(1,4)*tr(m)
              kmsu = einv(2,1)*ur(m) + einv(2,2)*uz(m)
     2             + einv(2,3)*tz(m) + einv(2,4)*tr(m)
              cintijr = e(i,1)*e(j,1)*kmpu*kmpu/(2.0*ra) 
     1         + (e(i,1)*e(j,2)+e(i,2)*e(j,1))*kmpu*kmsu/(ra+rb)
     2         + e(i,2)*e(j,2)*kmsu*kmsu/(2.0*rb)
            else if(typelyr .eq. 0)then
c-----
c             downward potentials coefficients at top of layer m
c-----
              km1pd = einv(3,1)*ur(m) + einv(3,2)*uz(m)
     1              + einv(3,3)*tz(m) + einv(3,4)*tr(m)
              km1sd = einv(4,1)*ur(m) + einv(4,2)*uz(m)
     1              + einv(4,3)*tz(m) + einv(4,4)*tr(m)
c-----
c             upward potentials coefficients at bottom of layer m
c-----
              kmpu = einv(1,1)*ur(m+1) + einv(1,2)*uz(m+1)
     1             + einv(1,3)*tz(m+1) + einv(1,4)*tr(m+1)
              kmsu = einv(2,1)*ur(m+1) + einv(2,2)*uz(m+1)
     1             + einv(2,3)*tz(m+1) + einv(2,4)*tr(m+1)
              FA=ffunc(ra,zd(m))
              GA=gfunc(ra,zd(m))
              FB=ffunc(rb,zd(m))
              GB=gfunc(rb,zd(m))
              H1=h1func(ra,rb,zd(m))
              H2=h2func(ra,rb,zd(m))
              cintijr = e(i,1)*e(j,1)*kmpu*kmpu*FA 
     1          + e(i,3)*e(j,3)*km1pd*km1pd*FA
     1          + e(i,2)*e(j,2)*kmsu*kmsu*FB 
     1          + e(i,4)*e(j,4)*km1sd*km1sd*FB
     4          + H1*((e(i,1)*e(j,2)+e(i,2)*e(j,1))*kmpu*kmsu +
     5             (e(i,3)*e(j,4)+e(i,4)*e(j,3))*km1pd*km1sd)
     6          + H2*((e(i,1)*e(j,4)+e(i,4)*e(j,1))*kmpu*km1sd +
     7             (e(i,2)*e(j,3)+e(i,3)*e(j,2))*km1pd*kmsu)
     8          + GA*(e(i,1)*e(j,3)+e(i,3)*e(j,1))*kmpu*km1pd
     9          + GB*(e(i,2)*e(j,4)+e(i,4)*e(j,2))*kmsu*km1sd
            else
c-----
c             downward potential coefficients for lower halfspace
c             based on the displacement, stress at bottom
c             Basically m = mmax
c-----
              km1pd = einv(3,1)*ur(m)+einv(3,2)*uz(m)
     2            +einv(3,3)*tz(m)+einv(3,4)*tr(m)
              km1sd = einv(4,1)*ur(m)+einv(4,2)*uz(m)
     2            +einv(4,3)*tz(m)+einv(4,4)*tr(m)
              cintijr = e(i,3)*e(j,3)*km1pd*km1pd/(2.0*ra) 
     1         +(e(i,3)*e(j,4)+e(i,4)*e(j,3))*km1pd*km1sd/(ra+rb)
     2         +e(i,4)*e(j,4)*km1sd*km1sd/(2.0*rb)
            endif
        endif

        intijr = dreal(cintijr)
        return
        end

        subroutine getdcdh(om2,wvno,wvno2,fac)
        implicit none
c-----
c       procedure parameters
c-----
        real*8 om2,wvno,wvno2,fac
c-----
c       common blocks
c-----
        integer NL
        parameter (NL=200)
        common/dmodl/  zd(NL),za(NL),zb(NL),zrho(NL),zqai(NL),zqbi(NL),
     1      zetap(NL),zetas(NL),zfrefp(NL),zfrefs(NL),
     2      xmu(NL), xlam(NL), mmax
        real*8 zd, za, zb, zrho, zqai, zqbi, zetap, zetas, 
     1      zfrefp, zfrefs,
     1      xmu, xlam
        integer mmax
        common/eigfun/ ur(NL),uz(NL),tz(NL),tr(NL),uu0(4),
     1                 dcda(NL),dcdb(NL),dcdr(NL),dcdh(NL)
        real*8 ur, uz, tz, tr, uu0
        real*8 dcda, dcdb, dcdr, dcdh
        common/wateri/iwat(NL)
        integer iwat
c-----
c       internal variables
c-----
        real*8 tur, tuz, ttz, ttr
        real*8 dfac, gfac1, gfac2, gfac3, gfac4, gfac5, gfac6
c  DEVELOPMENT
        real*8 drho, dmu, dlm
        real*8 duzdzp, dlur2, xl2mp, xl2mm
        real*8 dl2mu, duzdzm, drur2
        real*8 URB, DURDZM, DURDZP


        integer m
c-----

        do  m=1,mmax
            
            tuz = uz(m)
            ttz = tz(m)
            ttr = tr(m)
            if(iwat(m).eq.1)then
                tur = -wvno*ttz/(zrho(m)*om2)
            else
                tur = ur(m)
            endif
c-----
c       this assumes that the top is a halfspace
c-----
            if(m.eq.1)then
                drho = zrho(1) - 0.0
                dmu  = xmu(1) - 0.0
                dlm = xlam(1) - 0.0
                dl2mu = dlm + dmu + dmu
                xl2mp = xlam(m) + xmu(m) + xmu(m)
                duzdzp = (ttz + wvno*xlam(m)*tur)/xl2mp
                if(iwat(m) .eq.1)then
                    durdzp = wvno*tuz
                else
                    durdzp = (ttr/xmu(m)) - wvno*tuz
                endif
                drur2 = tur*tur*drho
                dlur2 = tur*tur*dl2mu

                gfac1 =  om2*drho*tuz**2
                gfac2 =  om2*drur2
                gfac3 = -wvno2*dmu*tuz**2
                gfac4 = -wvno2*dlur2
                gfac5 =  (xl2mp*duzdzp**2)
                gfac6 =  (xmu(m)*durdzp**2 )
            else
                drho = zrho(m) - zrho(m-1)
                dmu = xmu(m) - xmu(m-1)
                dlm = xlam(m) - xlam(m-1)
                dl2mu = dlm + dmu + dmu
                xl2mp = xlam(m)   + xmu(m)   + xmu(m)
                xl2mm = xlam(m-1) + xmu(m-1) + xmu(m-1)
                duzdzp = (ttz + wvno*xlam(m)*tur)/xl2mp
                if(xmu(m).eq.0.0)then
                    durdzp = wvno*tuz
                else
                    durdzp = (ttr/xmu(m)) - wvno*tuz
                endif
                if(xmu(m-1).eq.0.0 )then
                    durdzm = wvno*tuz
                else
                    durdzm = (ttr/xmu(m-1)) - wvno*tuz
                endif
c-----
c       attempt to fix for water layer, since Ur is not continuous
c       across fluid - solid interface or fluid-fluid interface
c-----
                if(iwat(m-1).eq.1 .and. iwat(m).eq.0 )then
                    URB = -wvno*tz(m)/(zrho(m-1)*om2)
                    drur2 = tur*tur*zrho(m)-URB*URB*zrho(m-1)
                    dlur2 = tur*tur*xl2mp -URB*URB*xl2mm
                    duzdzm = (ttz + wvno*xlam(m-1)*URB)/xlam(m-1)
                else if(iwat(m-1).eq.1 .and. iwat(m).eq.1 )then
                    URB = -wvno*tz(m)/(zrho(m-1)*om2)
                    drur2 = tur*tur*zrho(m)- URB*URB*zrho(m-1)
                    dlur2 = tur*tur*xl2mp  - URB*URB*xl2mm
                    duzdzm = (ttz + wvno*xlam(m-1)*URB)/xl2mm
                else
                    drur2 = tur*tur*drho
                    dlur2 = tur*tur*dl2mu
                    duzdzm = (ttz + wvno*xlam(m-1)*tur)/xl2mm
                endif


                gfac1 =  om2*drho*tuz**2
                gfac2 =  om2*drur2
                gfac3 = -wvno2*dmu*tuz**2
                gfac4 = -wvno2*dlur2
                gfac5 =  (xl2mp*duzdzp**2-xl2mm*duzdzm**2)
                gfac6 =  (xmu(m)*durdzp**2 - xmu(m-1)*durdzm**2)
            endif
            dfac = fac * (
     1          gfac1 + gfac2 + gfac3 + gfac4
     2          + gfac5 + gfac6 )
            if(dabs(dfac).lt.1.0d-38)then
                dfac = 0.0d+00
            endif
            dcdh(m) = dfac
        enddo
        return
        end
