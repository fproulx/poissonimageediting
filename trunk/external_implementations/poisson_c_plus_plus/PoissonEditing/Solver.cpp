// =============================================================================
// PoissonEditing - Poisson Image Editing for cloning and image estimation
//
// The following code implements:
// Exercise 1, Advanced Computer Graphics Course (Spring 2005)
// Tel-Aviv University, Israel
// http://www.cs.tau.ac.il/~tommer/adv-graphics/ex1.htm
//
// * Based on "Poisson Image Editing" paper, Pe'rez et. al. [SIGGRAPH/2003].
// * The code uses TAUCS, A sparse linear solver library by Sivan Toledo
//   (see http://www.tau.ac.il/~stoledo/taucs)
// =============================================================================
//
// COPYRIGHT NOTICE, DISCLAIMER, and LICENSE:
//
// PoissonEditing : Copyright (C) 2005, Tommer Leyvand (tommerl@gmail.com)
//
// Covered code is provided under this license on an "as is" basis, without
// warranty of any kind, either expressed or implied, including, without
// limitation, warranties that the covered code is free of defects,
// merchantable, fit for a particular purpose or non-infringing. The entire risk
// as to the quality and performance of the covered code is with you. Should any
// covered code prove defective in any respect, you (not the initial developer
// or any other contributor) assume the cost of any necessary servicing, repair
// or correction. This disclaimer of warranty constitutes an essential part of
// this license. No use of any covered code is authorized hereunder except under
// this disclaimer.
//
// Permission is hereby granted to use, copy, modify, and distribute this
// source code, or portions hereof, for any purpose, including commercial
// applications, freely and without fee, subject to the following restrictions: 
//
// 1. The origin of this software must not be misrepresented; you must not
//    claim that you wrote the original software. If you use this software
//    in a product, an acknowledgment in the product documentation would be
//    appreciated but is not required.
//
// 2. Altered source versions must be plainly marked as such, and must not be
//    misrepresented as being the original software.
//
// 3. This notice may not be removed or altered from any source distribution.
//

#include "Solver.h"
#include <map>
using std::map;
#include <iostream>
using namespace std;

#include <assert.h>

#include "../taucs/taucsaddon.h"

typedef unsigned int uint;

/**
 *	Clamps the input to [0..1].
 *	@return the clamped input
 */
float static inline clamp01(float f) {
	if (f < 0.0f) return 0.0f;
	else if (f > 1.0f) return 1.0f;
	else return f;
}

/**
 *	@see Solver.h
 */ 
void Solver::solve(const ImageBGRf& I, tBGRf c, ImageBGRf& O)
{
	uint x,y;

	uint w = I.getWidth();
	uint h = I.getHeight();

	// Build mapping from (x,y) to variables
	uint N = 0; // variable indexer
	map<uint,uint> mp;
	for (y = 1; y < h-1; y++) {
		for (x = 1; x < w-1; x++) {
			uint id = y*w+x;
			if (I.getPixel(x,y) == c) { // Masked pixel
				mp[id] = N;
				N++;
			}
		}
	}

	if (N == 0) {
		cout << "Solver::solve: No missing pixels found\n";
		return;
	}

	cout << "Solver::solve: Solving " << w << "x" << h << " with " << N << " unknowns" << endl;

	// Build the matrix
	// ----------------
	// We solve Ax = b for all 3 channels at once

	// Create the sparse matrix, we have at least 5 non-zero elements
	// per column
	taucs_ccs_matrix *pAt = taucs_ccs_create(N,N,5*N,TAUCS_DOUBLE);
	double* b = new double[3*N]; // All 3 channels at once
	uint n = 0;
	int index = 0;

	double* vals = pAt->taucs_values;
	int* rowptr = pAt->colptr;
	int* colind = pAt->rowind;

	// Populate matrix
	for (y = 1; y < h-1; y++) {
		for (x = 1; x < w-1; x++) {
			if (I.getPixel(x,y) == c) { // Variable
				uint id = y*w+x;
				rowptr[n] = index;

				// Right hand side is initialized to zero
				imagelib::tBGRf bb(0.0f, 0.0f, 0.0f);

				if (I.getPixel(x,y-1) == c) {
					vals[index] = 1.0f;
					colind[index] = mp[id-w];
					index++;
				} else {
					// Known pixel, update right hand side
					bb -= I.getPixel(x,y-1);
				}

				if (I.getPixel(x-1,y) == c) {
					vals[index] = 1.0f;
					colind[index] = mp[id-1];
					index++;
				} else {
					bb -= I.getPixel(x-1,y);
				}

				vals[index] = -4.0f;
				colind[index] = mp[id];
				index++;

				if (I.getPixel(x+1,y) == c) {
					vals[index] = 1.0f;
					colind[index] = mp[id+1];
					index++;
				} else {
					bb -= I.getPixel(x+1,y);
				}

				if (I.getPixel(x,y+1) == c) {
					vals[index] = 1.0f;
					colind[index] = mp[id+w];
					index++;
				} else {
					bb -= I.getPixel(x,y+1);
				}

				uint i = mp[id];
				// Spread the right hand side so we can solve using TAUCS for
				// 3 channels at once.
				b[i] = bb.b;
				b[i+N] = bb.g;
				b[i+2*N] = bb.r;
				n++;
			}
		}
	}

	assert(n == N);
	rowptr[n] = index; // mark last CRS index

	taucs_ccs_matrix *pA = MatrixTranspose(pAt);
	double* u = new double[3*N];

	char* options[] = { "taucs.factor.LU=true", NULL }; 
	if (taucs_linsolve(pA, NULL, 3, u, b, options, NULL) != TAUCS_SUCCESS) {
		cout << "Solving failed\n";
	}

	// Convert solution vector back to image
	O = I;
	for (y = 1; y < h-1; y++) {
		for (x = 1; x < w-1; x++) {
			if (I.getPixel(x,y) == c) {
				uint id = y*w+x;
				uint ii = mp[id];
				imagelib::tBGRf p;
				// Clamp RGB values to [0..1]
				p.b = clamp01((float)u[ii]);
				p.g = clamp01((float)u[ii+N]);
				p.r = clamp01((float)u[ii+2*N]);
				O.setPixel(x,y,p);
			}
		}
	}
}

/**
 *	@see Solver.h
 */ 
void Solver::solve(const ImageBGRf& I, const ImageLb& M, const ImageBGRf& div,
				   ImageBGRf& O)
{
	uint x,y;

	uint w = I.getWidth();
	uint h = I.getHeight();

	// Build mapping from (x,y) to variables
	uint N = 0; // variable indexer
	map<uint,uint> mp;
	for (y = 1; y < h-1; y++) {
		for (x = 1; x < w-1; x++) {
			uint id = y*w+x;
			if (M.getPixel(x,y) != MASK_BG) { // Masked pixel
				mp[id] = N;
				N++;
			}
		}
	}

	if (N == 0) {
		cout << "Solver::solve: No masked pixels found (mask color is non-black)\n";
		return;
	}

	cout << "Solver::solve: Solving " << w << "x" << h << " with " << N << " unknowns" << endl;

	// Build the matrix
	// ----------------
	// We solve Ax = b for all 3 channels at once

	// Create the sparse matrix, we have at least 5 non-zero elements
	// per column

	taucs_ccs_matrix *pAt = taucs_ccs_create(N,N,5*N,TAUCS_DOUBLE);
	double* b = new double[3*N];// All 3 channels at once
	uint n = 0;
	int index = 0;

	double* vals = pAt->taucs_values;
	int* rowptr = pAt->colptr;
	int* colind = pAt->rowind;

	// Populate matrix
	for (y = 1; y < h-1; y++) {
		for (x = 1; x < w-1; x++) {
			if (M.getPixel(x,y) != MASK_BG) {
				uint id = y*w+x;
				rowptr[n] = index;
				imagelib::tBGRf bb = div.getPixel(x,y);

				if (M.getPixel(x,y-1) != MASK_BG) {
					vals[index] = 1.0f;
					colind[index] = mp[id-w];
					index++;
				} else {
					// Known pixel, update right hand side
					bb -= I.getPixel(x,y-1);
				}

				if (M.getPixel(x-1,y) != MASK_BG) {
					vals[index] = 1.0f;
					colind[index] = mp[id-1];
					index++;
				} else {
					bb -= I.getPixel(x-1,y);
				}

				vals[index] = -4.0f;
				colind[index] = mp[id];
				index++;

				if (M.getPixel(x+1,y) != MASK_BG) {
					vals[index] = 1.0f;
					colind[index] = mp[id+1];
					index++;
				} else {
					bb -= I.getPixel(x+1,y);
				}

				if (M.getPixel(x,y+1) != MASK_BG) {
					vals[index] = 1.0f;
					colind[index] = mp[id+w];
					index++;
				} else {
					bb -= I.getPixel(x,y+1);
				}

				uint i = mp[id];
				// Spread the right hand side so we can solve using TAUCS for
				// 3 channels at once.
				b[i] = bb.b;
				b[i+N] = bb.g;
				b[i+2*N] = bb.r;
				n++;
			}
		}
	}

	assert(n == N);
	rowptr[n] = index; // mark last CRS index

	taucs_ccs_matrix *pA = MatrixTranspose(pAt);
	double* u = new double[3*N];

	char* options[] = { "taucs.factor.LU=true", NULL }; 
	if (taucs_linsolve(pA, NULL, 3, u, b, options, NULL) != TAUCS_SUCCESS) {
		cout << "Solving failed\n";
	}

	// Convert solution vector back to image
	O = I;
	for (y = 1; y < h-1; y++) {
		for (x = 1; x < w-1; x++) {
			if (M.getPixel(x,y) != MASK_BG) {
				uint id = y*w+x;
				uint ii = mp[id];
				imagelib::tBGRf p;
				// Clamp RGB values to [0..1]
				p.b = clamp01((float)u[ii]);
				p.g = clamp01((float)u[ii+N]);
				p.r = clamp01((float)u[ii+2*N]);
				O.setPixel(x,y,p);
			}
		}
	}
}