/* NetHack 3.6	system.h	$NHDT-Date: 1550268586 2019/02/15 22:09:46 $  $NHDT-Branch: NetHack-3.6.2-beta01 $:$NHDT-Revision: 1.17 $ */
/* Copyright (c) Stichting Mathematisch Centrum, Amsterdam, 1985. */
/*-Copyright (c) Robert Patrick Rankin, 2017. */
/* NetHack may be freely redistributed.  See license for details. */

#ifndef SYSTEM_H
#define SYSTEM_H

#if !defined(__cplusplus) && !defined(__GO32__)

#define E extern

#include <stdlib.h>  /* For srand48 */
#include <unistd.h>  /* For sleep */

/* Define srand48 accurately */
#ifndef HAVE_SRAND48
E void srand48(long int);
#endif

/* Define sleep accurately */
#ifndef HAVE_SLEEP
E unsigned int sleep(unsigned int);
#endif

#endif /* !__cplusplus && !__GO32__ */

#endif /* SYSTEM_H */