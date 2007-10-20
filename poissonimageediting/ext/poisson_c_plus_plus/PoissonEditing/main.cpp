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

#include <windows.h>
#include <time.h>

#include "../imagelib/imagelib.h"
#include "../imagelib/GraphicsEx.h"

#include "Solver.h"
#include <iostream>
using std::cout;

using imagelib::ImageBGRb;
using imagelib::ImageLb;
using imagelib::LoadImageFailedException;
using imagelib::SaveImageFailedException;
using imagelib::GraphicsEx;

typedef unsigned int uint;

/**
 *	Print usage help to stdout.
 */
void usage()
{
	cout << "Poisson Cloning\n"
		 << "-------------------\n"
		 << "Usage:\n"
		 << " pediting -complete [...] or -clone [...] or -rndLines\n\n"
		 << "  -complete: to complete a missing area via Poisson\n"
		 << "  -clone:    Poisson cloning from one image to another\n"
		 << "  -rndLines: to randomly sample lines from an image\n"
		 << " pediting -complete <source image> Cr Cb Cg <output image>\n"
		 << " Completes the missing area denoted by RGB color <Cr,Cb,Cg> using Poisson\n\n"
		 << " pediting -clone <source image> <source mask> <target image> x y <output image>\n"
		 << " Clones the masked area of the source image onto the target using Poisson\n"
		 << "  where:\n"
		 << "\t <source image> the source image file\n"
		 << "\t <source mask>  the mask for the area to copy from the source image\n"
		 << "\t <target image> the target image file (paste to)\n"
		 << "\t x y            paste offset\n"
		 << "\t <output image> the output image file\n\n"
		 << " pediting -rndLines <source image> Cr Cb Cg <numLines> <output image>\n";
}

/**
 *	Verifies the validity of the mask. Checks that:
 *  - The binary mask does not translate outside the target image.
 *	- There is no mask on the boundary.
 *	@return 'false' if verfication failed, 'true' if verification
 *          passed (maybe with warnings).
 */
bool verifyMask(ImageLb& SM, uint tw, uint th, uint ox, uint oy)
{
	uint w = SM.getWidth();
	uint h = SM.getHeight();
	
	bool reportedBadColor = false;

	// Verify binary mask
	for (uint y = 0; y < h; y++) {
		for (uint x = 0; x < w; x++) {
			imagelib::byte b = SM.getPixel(x,y);
			if (b != MASK_BG && ((x+ox) >= tw || (y+oy) >= th)) {
				cout << "Error: Source mask translates outside target image" << endl;
				return false;
			}
		}
	}

	// Verify no mask on the boundary
	for (uint x = 0; x < w; x++) {
		if (SM.getPixel(x,0) != MASK_BG || SM.getPixel(x,h-1) != MASK_BG) {
			cout << "Warning: Mask must not be set on the image boundary" << endl;
			return true;
		}
	}
	for (uint y = 0; y < h; y++) {
		if (SM.getPixel(0,y) != MASK_BG || SM.getPixel(w-1,y) != MASK_BG) {
			cout << "Warning: Mask must not be set on the image boundary" << endl;
			return true;
		}
	}

	return true;
}

/**
 *	Complete/Estimate the missing areas of the image by setting the guidance
 *  field to 0.
 *	See class slides at http://www.cs.tau.ac.il/~tommer/adv-graphics/ex1.htm
 *	or "Poisson Image Editing" paper.
 */
int doComplete(char* argv[])
{
	// Parse arguments
	const char* simageFname = argv[1];
	imagelib::tBGRf c;
	c.r = (float)atol(argv[2])/255.0f;
	c.g = (float)atol(argv[3])/255.0f;
	c.b = (float)atol(argv[4])/255.0f;
	const char* oimageFname = argv[5];

	// Load input image
	ImageBGRb I;
	try {
		imagelib::load(simageFname, I);
	} catch (LoadImageFailedException& ex) {
		cout << "Error: Failed loading image '" << ex.what() << "'" << endl;
		return -1;
	}

	// Run poisson solver to fill missing areas
	ImageBGRf O;
	Solver::solve(imagelib::convert(I), c, O);
	try {
		imagelib::save(oimageFname, imagelib::convert(O));
	} catch (SaveImageFailedException& ex) {
		cout << "Error: Failed saving result image '" << ex.what() << "'" << endl;
		return -1;
	}

	return -1;
}

/**
 *	Clone the masked area of the source image onto the target image by using
 *  the gradients in the source image as a guidance field.
 *	See class slides at http://www.cs.tau.ac.il/~tommer/adv-graphics/ex1.htm
 *	or "Poisson Image Editing" paper.
 */
int doClone(char* argv[]) {
	// Parse arguments
	const char* simageFname = argv[1];
	const char* smaskFname = argv[2];
	const char* timageFname = argv[3];
	int ox = atol(argv[4]);
	int oy = atol(argv[5]);
	const char* oimageFname = argv[6];

	// Load input images
	ImageBGRb SI, TI;
	ImageLb SM;

	try {
		imagelib::load(simageFname, SI);
		imagelib::load(smaskFname, SM);
		imagelib::load(timageFname, TI);
	} catch (LoadImageFailedException& ex) {
		cout << "Error: Failed loading image '" << ex.what() << "'" << endl;
		return -1;
	}

	// Verify input
	if (!SI.sameDimensions(SM)) {
		cout << "Error: Input image and input mask must have the same dimensions" << endl;
		return -1;
	}
	if (!verifyMask(SM, TI.getWidth(), TI.getHeight(), ox, oy)) {
		// Error text printed by verifyMask
		return -1;
	}
	
	// Translate the source image and the source mask
	ImageBGRb TSI; // TSI = Translated Source Image
	ImageLb TSM;   // TSM = Translated Source Mask
	TSI.create(TI.getWidth(), TI.getHeight());
	TSM.create(TI.getWidth(), TI.getHeight(), (imagelib::byte)0);
	for (uint y = 0; y < TSI.getHeight(); y++) {
		for (uint x = 0; x < TSI.getWidth(); x++) {
			if (SI.within(x-ox,y-oy)) {
				TSI.setPixel(x,y,SI.getPixel(x-ox,y-oy));
				TSM.setPixel(x,y,SM.getPixel(x-ox,y-oy));
			}
		}
	}

	// Run poisson solver on the divergence field on the translated source image
	// (TSI)
	ImageBGRf O;
	Solver::solve(imagelib::convert(TI), TSM,
				  imagelib::divergents(imagelib::gradient(imagelib::convert(TSI))), O);

	/*
	// The above code is equal to just calculating the laplacian
	Solver::solve(imagelib::convert(TI), TSM,
		imagelib::laplacian(imagelib::convert(TSI)), O);
	*/

	try {
		imagelib::save(oimageFname, imagelib::convert(O));
	} catch (SaveImageFailedException& ex) {
		cout << "Error: Failed saving result image '" << ex.what() << "'" << endl;
		return -1;
	}
	

	return 0;
}

void rndRectPoint(int w, int h, int&x, int& y) {
	// Choose horizontal or vertical
	if (rand() % 2) {
		// Horizontal
		y = (rand() % 2) == 0 ? 0 : h-1;
		x = rand() % w;
	} else {
		x = (rand() % 2) == 0 ? 0 : w-1;
		y = rand() % h;
	}
}

int doRndLines(char* argv[]) {
	srand((unsigned)time(NULL));

	const char* simageFname = argv[1];
	imagelib::tBGRb c;
	c.r = (byte)atol(argv[2]);
	c.g = (byte)atol(argv[3]);
	c.b = (byte)atol(argv[4]);
	uint numLines = (uint)atol(argv[5]);
	const char* oimageFname = argv[6];

	ImageBGRb I,O;
	try {
		imagelib::load(simageFname, I);
	} catch (LoadImageFailedException& ex) {
		cout << "Error: Failed loading image '" << ex.what() << "'" << endl;
		return -1;
	}

	Image* II;
	imagelib::convert(I, II);

	int w = (int)I.getWidth();
	int h = (int)I.getHeight();

	TextureBrush brush(II, WrapModeTile, 0, 0, w, h);
	O.create((uint)w, (uint)h, c);
	GraphicsEx g(O);

	Pen p(&brush);

	// Draw boundary rectangle
	g.DrawRectangle(&p, 0, 0, w-1, h-1);
	// Draw random lines
	for (uint i = 0; i < numLines; i++) {
		int x1,y1,x2,y2;
		rndRectPoint(w,h,x1,y1);
		rndRectPoint(w,h,x2,y2);
		g.DrawLine(&p, x1, y1, x2, y2);
	}
	
	delete II;

	try {
		imagelib::save(oimageFname, O);
	} catch (SaveImageFailedException& ex) {
		cout << "Error: Failed saving result image '" << ex.what() << "'" << endl;
		return -1;
	}

	return 0;
}

int main(int argc, char* argv[])
{
	if (argc < 2) {
		usage();
		return -1;
	}

	try {
		// Initialize GDI+ (shutdown automatic in dtor)
		imagelib::GdiplusInitializer gdiplusInitializer;

		char* cmd = argv[1];
		if (argc == 7 && !stricmp(cmd, "-complete")) {
			return doComplete(argv+1);
		} else if (argc == 8 && !stricmp(cmd, "-clone")) {
			return doClone(argv+1);
		} else if (argc == 8 && !stricmp(cmd, "-rndLines")) {
			return doRndLines(argv+1);
		} else {
			usage();
			return -1;
		}
	} catch (imagelib::GdiplusInitializationException& ex) {
		cout << ex.what() << endl;
	} catch (...) {
		cout << "Unknown exception caused termination" << endl;
	}
}