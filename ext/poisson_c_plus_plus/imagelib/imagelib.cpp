// =============================================================================
// imagelib - Image library with GDI+ wrapper (Version 1.0)
//
// A simple, lightweight image library with a GDI+ wrapper.
// Supports BMP,JPG,GIF,TIFF,PNG (through GDI+)
// =============================================================================
//
// COPYRIGHT NOTICE, DISCLAIMER, and LICENSE:
//
// imagelib : Copyright (C) 2005, Tommer Leyvand (tommerl@gmail.com)
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

#include "imagelib.h"

#include <windows.h>
#include <GdiPlus.h>
#pragma comment (lib,"Gdiplus.lib")
using namespace Gdiplus;

#include <math.h>

#include "GraphicsEx.h"

namespace imagelib {

int GetEncoderClsid(const WCHAR* format, CLSID* pClsid);

static Gdiplus::Image* loadImageInternal(const char* fname) {
	// Conver fname to WCHAR
	WCHAR* wFname = new WCHAR[strlen(fname)+1];
	int nLen = MultiByteToWideChar(CP_ACP, 0,fname, -1, NULL, NULL);
	MultiByteToWideChar(CP_ACP, 0, fname, -1, wFname, nLen);

	Gdiplus::Image* I = Gdiplus::Image::FromFile(wFname);
	delete[] wFname;

	if (I == NULL) 
		THROW(LoadImageFailedException("Failed loading image", fname));

	Status s = I->GetLastStatus();
	switch (s) {
		case Ok:
			break;
		case FileNotFound:
			delete I;
			THROW(LoadImageFailedException("Image file not found", fname, s));
		case GdiplusNotInitialized:
			delete I;
			THROW(LoadImageFailedException("GDI+ was not initialized", fname, s));
		case UnknownImageFormat:
			delete I;
			THROW(LoadImageFailedException("Unsupported image format", fname, s));
		default:
			delete I;
			THROW(LoadImageFailedException("Load image failed with unknown reason code", fname, s));
	}

	return I;
}

void load(const char* fname, ImageBGRb& I, bool allowConvert)
{
	Gdiplus::Image* II = loadImageInternal(fname);

	PixelFormat pixelFormat = II->GetPixelFormat();
	// Fail if conversion is not allowed
	if (!allowConvert &&
		pixelFormat != PixelFormat24bppRGB &&
		pixelFormat != PixelFormat32bppRGB) {
		delete II;
		THROW(LoadImageNoConvertException("Wrong pixel format with no conversion set", fname, pixelFormat));
	}

	I.create(II->GetWidth(),II->GetHeight());
	GraphicsEx g(I);
	g.DrawImage(II, 0, 0, II->GetWidth(), II->GetHeight());

	delete II;
}

void load(const char* fname, ImageLb& I, bool allowConvert)
{
	Gdiplus::Image* II = loadImageInternal(fname);

	PixelFormat pixelFormat = II->GetPixelFormat();
	// Fail if conversion is not allowed
	if (!allowConvert &&
		pixelFormat != PixelFormat8bppIndexed &&
		pixelFormat != PixelFormat4bppIndexed &&
		pixelFormat != PixelFormat1bppIndexed &&
		pixelFormat != PixelFormat16bppGrayScale) {
		delete II;
		THROW(LoadImageNoConvertException("Wrong pixel format with no conversion set", fname, pixelFormat));
	}

	ImageBGRb I2(II->GetWidth(), II->GetHeight());
	GraphicsEx g(I2);
	g.DrawImage(II, 0, 0, II->GetWidth(), II->GetHeight());

	grayscale(I2, I);

	delete II;
}

static tImageType typeFromExt(const char* fname) {
	char* ext = strrchr(fname, '.');
	if (ext == NULL) {
		THROW(SaveImageFailedException("Can not determine image extension", fname));
	}

	tImageType imType = AUTO;
	if (!stricmp(ext, ".bmp")) {
		imType = BMP;
	} else if (!stricmp(ext,".jpg") || !stricmp(ext,".jpeg")) {
		imType = JPG;
	} else if (!stricmp(ext, ".gif")) {
		imType = GIF;
	} else if (!stricmp(ext,".tif") || !stricmp(ext,".tiff")) {
		imType = TIFF;
	} else if (!stricmp(ext, ".png")) {
		imType = PNG;
	} else {
		THROW(SaveImageFailedException("Can not determine image type from extension", fname));
	}
	return imType;
}

static const WCHAR* type2mimeType[LAST] = {L"image/bmp", L"image/jpeg", L"image/gif", L"image/tiff", L"image/png"};

static void saveImageInternal(Gdiplus::Image* I, tImageType imageType, const char* fname) {
	if (imageType == AUTO) {
		// Find the correct type based on the extension
		imageType = typeFromExt(fname);
	}

	CLSID clsid;
	int i = GetEncoderClsid(type2mimeType[imageType], &clsid);
	if (i < 0)
		THROW(SaveImageFailedException("Failed detecting clsid from mime type", fname));

	// Conver fname to WCHAR
	WCHAR* wFname = new WCHAR[strlen(fname)+1];
	int nLen = MultiByteToWideChar(CP_ACP, 0,fname, -1, NULL, NULL);
	MultiByteToWideChar(CP_ACP, 0, fname, -1, wFname, nLen);

	Status s;
	if (imageType == JPG) {
		EncoderParameters encodersParams;
		encodersParams.Count = 1;
		encodersParams.Parameter[0].Guid = EncoderQuality;
		encodersParams.Parameter[0].Type = EncoderParameterValueTypeLong;
		encodersParams.Parameter[0].NumberOfValues = 1;
		ULONG quality = 100;
		encodersParams.Parameter[0].Value = &quality;
		s = I->Save(wFname, &clsid, &encodersParams);
	} else {
		s = I->Save(wFname, &clsid);
	}
	switch (s) {
		case Ok:
			break;
		case GdiplusNotInitialized:
			THROW(SaveImageFailedException("GDI+ was not initialized", fname, s));
		default:
			THROW(SaveImageFailedException("Save image failed with unknown reason code", fname, s));
	}
	delete[] wFname;
}

void save(const char* fname, const ImageBGRb& I, tImageType imageType)
{
	Gdiplus::Image* II = NULL;
	convert(I, II);

	__try {
		saveImageInternal(II, imageType, fname);
	} __finally {
		delete II;
	}
}

void save(const char* fname, const ImageLb& I, tImageType imageType)
{
	Gdiplus::Image* II = NULL;
	convert(I, II);

	__try {
		saveImageInternal(II, imageType, fname);
	} __finally {
		delete II;
	}
}

GdiplusInitializer::GdiplusInitializer() {
	// Initialize GDI+
	GdiplusStartupInput gdiplusStartupInput;
	Status s = GdiplusStartup(&m_gdiplusToken, &gdiplusStartupInput, NULL);
	if (s != Ok) {
		THROW(GdiplusInitializationException("Failed initializing GDI+", s));
	}
}

GdiplusInitializer::~GdiplusInitializer() {
	GdiplusShutdown(m_gdiplusToken);
}

void convert(const ImageBGRb& I, ImageBGRf& O)
{
	uint w = I.getWidth();
	uint h = I.getHeight();
	O.create(w, h);

	for (uint y = 0; y < h; y++) {
		for (uint x = 0; x < w; x++) {
            tBGRb bgrb = I.getPixel(x,y);
			tBGRf bgrf;
			bgrf.b = (float)bgrb.b / 255.0f;
			bgrf.g = (float)bgrb.g / 255.0f;
			bgrf.r = (float)bgrb.r / 255.0f;
			O.setPixel(x,y,bgrf);
		}
	}
}

ImageBGRf convert(const ImageBGRb& I)
{
	ImageBGRf O;
	convert(I,O);
	return O;
}

inline byte clampConvert(float f) {
	if (f < 0.0f) return 0;
	if (f > 1.0f) return 255;
	return (byte)(f*255.0f);
}

void convert(const ImageBGRf& I, ImageBGRb& O)
{
	uint w = I.getWidth();
	uint h = I.getHeight();
	O.create(w, h);

	for (uint y = 0; y < h; y++) {
		for (uint x = 0; x < w; x++) {
			tBGRf bgrf = I.getPixel(x,y);
			tBGRb bgrb;
			bgrb.b = clampConvert(bgrf.b);
			bgrb.g = clampConvert(bgrf.g);
			bgrb.r = clampConvert(bgrf.r);
			O.setPixel(x,y,bgrb);
		}
	}
}

ImageBGRb convert(const ImageBGRf& I)
{
	ImageBGRb O;
	convert(I,O);
	return O;
}

void convert(const ImageLb& I, ImageLf& O)
{
	uint w = I.getWidth();
	uint h = I.getHeight();
	O.create(w, h);

	for (uint y = 0; y < h; y++) {
		for (uint x = 0; x < w; x++) {
			float f = (float)I.getPixel(x,y) / 255.0f;
			O.setPixel(x,y,f);
		}
	}
}

ImageLf convert(const ImageLb& I)
{
	ImageLf O;
	convert(I,O);
	return O;
}

void convert(const ImageLf& I, ImageLb& O)
{
	uint w = I.getWidth();
	uint h = I.getHeight();
	O.create(w, h);

	for (uint y = 0; y < h; y++) {
		for (uint x = 0; x < w; x++) {
			O.setPixel(x,y,clampConvert(I.getPixel(x,y)));
		}
	}
}

ImageLb convert(const ImageLf& I)
{
	ImageLb O;
	convert(I,O);
	return O;
}

void convert(const Gdiplus::Image* I, ImageBGRb& O)
{
	Gdiplus::Image* II = const_cast<Gdiplus::Image*>(I);
	O.create(II->GetWidth(), II->GetHeight());
	GraphicsEx g(O);

	g.DrawImage(II, 0, 0, II->GetWidth(), II->GetHeight());
}

void convert(const ImageBGRb& I, Gdiplus::Image*& O, bool clone)
{
	tBGRb* ptr = const_cast<ImageBGRb&>(I).ptr();
	if (clone) {
		ptr = new tBGRb[I.getPtrSize()];
		std::copy(I.ptr(), I.ptr() + I.getPtrSize(), ptr);
	}
	O = new Gdiplus::Bitmap(I.getWidth(), I.getHeight(), I.getStride(), PixelFormat24bppRGB, (BYTE*)ptr);
	ASSERT(O->GetLastStatus() == Gdiplus::Ok);
}

void convert(const Gdiplus::Image* I, ImageLb& O)
{
	Gdiplus::Image* II = const_cast<Gdiplus::Image*>(I);
	ImageBGRb O2(II->GetWidth(), II->GetHeight());
	convert(II, O2);
	grayscale(O2, O);
}

void convert(const ImageLb& I, Gdiplus::Image*& O)
{
	O = new Gdiplus::Bitmap(I.getWidth(), I.getHeight(), I.getStride(), PixelFormat8bppIndexed, (BYTE*)I.ptr());
	ASSERT(O->GetLastStatus() == Gdiplus::Ok);
}

void splitBlue(const ImageBGRb& I, ImageLb& O)
{
	uint w = I.getWidth();
	uint h = I.getHeight();
	O.create(w, h);

	for (uint y = 0; y < h; y++) {
		for (uint x = 0; x < w; x++) {
			O.setPixel(x,y,I.getPixel(x,y).b);
		}
	}
}
ImageLb splitBlue(const ImageBGRb& I)
{
	ImageLb O;
	splitBlue(I,O);
	return O;
}

void splitGreen(const ImageBGRb& I, ImageLb& O)
{
	uint w = I.getWidth();
	uint h = I.getHeight();
	O.create(w, h);

	for (uint y = 0; y < h; y++) {
		for (uint x = 0; x < w; x++) {
			O.setPixel(x,y,I.getPixel(x,y).g);
		}
	}
}
ImageLb splitGreen(const ImageBGRb& I)
{
	ImageLb O;
	splitGreen(I,O);
	return O;
}

void splitRed(const ImageBGRb& I, ImageLb& O)
{
	uint w = I.getWidth();
	uint h = I.getHeight();
	O.create(w, h);

	for (uint y = 0; y < h; y++) {
		for (uint x = 0; x < w; x++) {
			O.setPixel(x,y,I.getPixel(x,y).r);
		}
	}
}
ImageLb splitRed(const ImageBGRb& I)
{
	ImageLb O;
	splitRed(I,O);
	return O;
}

void merge(const ImageLb& B, const ImageLb& G, const ImageLb& R, ImageBGRb& O)
{
	uint w = B.getWidth();
	uint h = B.getHeight();
	O.create(w, h);

	for (uint y = 0; y < h; y++) {
		for (uint x = 0; x < w; x++) {
			tBGRb p;
			p.b = B.getPixel(x,y);
			p.g = G.getPixel(x,y);
			p.r = R.getPixel(x,y);
			O.setPixel(x,y,p);
		}
	}
}

ImageBGRb merge(const ImageLb& B, const ImageLb& G, const ImageLb& R)
{
	ImageBGRb O;
	merge(B,G,R,O);
	return O;
}

void grayscale(const ImageBGRb& I, ImageLb& O)
{
	uint w = I.getWidth();
	uint h = I.getHeight();
	O.create(w, h);

	for (uint y = 0; y < h; y++) {
		for (uint x = 0; x < w; x++) {
			tBGRb bgrb = I.getPixel(x,y);
			O.setPixel(x,y,(bgrb.b + bgrb.g + bgrb.r)/3);
		}
	}
}

ImageLb grayscale(const ImageBGRb& I)
{
	ImageLb O;
	grayscale(I,O);
	return O;
}

int GetEncoderClsid(const WCHAR* format, CLSID* pClsid)
{
	UINT  num = 0;          // number of image encoders
	UINT  size = 0;         // size of the image encoder array in bytes

	Gdiplus::ImageCodecInfo* pImageCodecInfo = NULL;

	Gdiplus::GetImageEncodersSize(&num, &size);
	if(size == 0)
		return -1;  // Failure

	pImageCodecInfo = (Gdiplus::ImageCodecInfo*)(malloc(size));
	if(pImageCodecInfo == NULL)
		return -1;  // Failure

	Gdiplus::GetImageEncoders(num, size, pImageCodecInfo);

	for(UINT j = 0; j < num; ++j)
	{
		if( wcscmp(pImageCodecInfo[j].MimeType, format) == 0 )
		{
			*pClsid = pImageCodecInfo[j].Clsid;
			free(pImageCodecInfo);
			return j;  // Success
		}    
	}

	free(pImageCodecInfo);
	return -1;  // Failure
}

void RGBtoHSV(tBGRb c, double& h, double& s, double& v)
{
	double r = (double)c.r / 255.0;
	double g = (double)c.g / 255.0;
	double b = (double)c.b / 255.0;

	double min, max, delta;
	min = min( r, min(g, b ));
	max = max( r, max(g, b ));
	delta = max - min;
	v = max;
	s = 0;
	if (max > 0)
		s = delta / max;
	h = 0;
	if (delta > 0) {
		if( r == max )
			h = ( g - b ) / delta;		// between yellow & magenta
		else if( g == max )
			h = 2 + ( b - r ) / delta;	// between cyan & yellow
		else
			h = 4 + ( r - g ) / delta;	// between magenta & cyan
		h *= 60;				// degrees
		if( h < 0 )
			h += 360;
	}
}

tBGRb HSVtoRGB(double h, double s, double v)
{
	int i;
	double f, p, q, t,r,g,b;
	if( s == 0 ) {
		// achromatic (grey)
		r = g = b = v;
	} else {
		h /= 60;			// sector 0 to 5
		i = (int)floor( h );
		f = h - i;			// factorial part of h
		p = v * ( 1 - s );
		q = v * ( 1 - s * f );
		t = v * ( 1 - s * ( 1 - f ) );
		switch( i ) {
		case 0:
			r = v;
			g = t;
			b = p;
			break;
		case 1:
			r = q;
			g = v;
			b = p;
			break;
		case 2:
			r = p;
			g = v;
			b = t;
			break;
		case 3:
			r = p;
			g = q;
			b = v;
			break;
		case 4:
			r = t;
			g = p;
			b = v;
			break;
		default:		// case 5:
			r = v;
			g = p;
			b = q;
			break;
		}
	}

	// scale back to 0..255
	r *= 255;
	g *= 255;
	b *= 255;

	return tBGRb((byte)b, (byte)g, (byte)r);
}

} // namespace image