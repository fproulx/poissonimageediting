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

#pragma once

#include "Image.h"
#include <stdexcept>

namespace Gdiplus{
	class Image;
}

namespace imagelib {
typedef unsigned int uint;
typedef unsigned char byte;

/**
 *	Basic RGB template type.
 */
template<class T>
struct tBGR {
	tBGR() {}
	tBGR(T b, T g, T r) : b(b), g(g), r(r) {}

	T b;
	T g;
	T r;

	tBGR<T>& operator+=(const tBGR<T>& O);
	tBGR<T> operator+(const tBGR<T>& O) const;
	tBGR<T>& operator-=(const tBGR<T>& O);
	tBGR<T> operator-(const tBGR<T>& O) const;
	tBGR<T>& operator*=(const T& t);
	tBGR<T> operator*(const T& t) const;
	tBGR<T>& operator/=(const T& t);
	tBGR<T> operator/(const T& t) const;



	bool operator==(const tBGR<T>& O) const;
};

/* RGB 8bit byte */
typedef tBGR<byte> tBGRb;
/* RGB 32bit float */
typedef tBGR<float> tBGRf;

/**
*	Basic UV template type.
*/
template<class T>
struct tUV {
	tUV() {}
	tUV(T u, T v) : u(u), v(v) {}

	T u;
	T v;
};

/* RGB 8bit byte */
typedef tUV<byte> tUVb;
/* RGB 32bit float */
typedef tUV<float> tUVf;

//////////////////////////////////////////////////////////////////////////

typedef Image<tBGRb> ImageBGRb;
typedef Image<tUVb> ImageUVb;
typedef Image<byte> ImageLb;


typedef Image<tBGRf> ImageBGRf;
typedef Image<tUVf> ImageUVf;
typedef Image<float> ImageLf;

//////////////////////////////////////////////////////////////////////////

/**
 *	Exception thrown when GDI+ initializer fails.
 */
class GdiplusInitializationException : public std::exception {
public:
	/**
	 *  Ctor.
	 *	@param msg the exception error message.
	 *	@param status the underlying GDI+ status code.
	 */
	GdiplusInitializationException(const char* msg, int status = -1)
		: std::exception(msg), status(status) {}

	const int status;
};

/**
 *  GDI+ initializer class.
 *	GDI+ is initialized during construction and shutdowns during destruction.
 */
class GdiplusInitializer {
public:
	/**
	 *  Initializes GDI+.
	 *	@throws GdiplusInitializationException on failure.
	 */
	GdiplusInitializer();

	/**
	 *  Shuts downs GDI+.
	 */
	~GdiplusInitializer();
private:
	/// The GDI+ token
	unsigned long m_gdiplusToken;
};

//////////////////////////////////////////////////////////////////////////

/**
 *	Exception thrown when image load operation fails.
 */
class LoadImageFailedException : public std::exception {
public:
	/**
	 *  Ctor.
	 *	@param msg the exception error message.
	 *	@param fname the image filename that caused this exception.
	 *	@param status the underlying GDI+ status code.
	 */
	LoadImageFailedException(const char* msg, const char* fname, int status = -1)
		: std::exception(msg), fname(fname), status(status) {}

	const char* const fname;
	const int status;
};

/**
 *	Exception thrown when image load is attempted for
 *  an incompatible BPP and conversion is not allowed.
 */
class LoadImageNoConvertException : public LoadImageFailedException {
public:
	/**
	 *  Ctor.
	 *	@param msg the exception error message.
	 *	@param fname the image filename that caused this exception.
	 *	@param pixelFormat the underlying GDI+ pixel format.
	 */
	LoadImageNoConvertException(const char* msg, const char* fname, int pixelFormat)
		: LoadImageFailedException(msg, fname), pixelFormat(pixelFormat) {}

	const int pixelFormat;
};

/**
 *	Loads the image from the specified filename.
 *	@param fname the image filename to load (image type will be automatically detected).
 *	@param I	 the loaded image.
 *	@param allowConvert 'true' to allow conversion of the loaded file to the specified
 *         format, 'false' to fail with an exception.
 *	@throw LoadImageFailedException if image loading fails
 *	@throw LoadImageNoConvertException if image BPP differs from expected and
 *									   conversion is not allowed.
 */
void load(const char* fname, ImageBGRb& I, bool allowConvert = true);
void load(const char* fname, ImageLb& I, bool allowConvert = true);

/**
 *	Exception thrown when image save operation fails.
 */
class SaveImageFailedException : public std::exception {
public:
	/**
	 *  Ctor.
	 *	@param msg the exception error message.
	 *	@param fname the image filename that caused this exception.
	 *	@param status the underlying GDI+ status code.
	 */
	SaveImageFailedException(const char* msg, const char* fname, int status = -1)
		: std::exception(msg), fname(fname), status(status) {}

	const char* const fname;
	const int status;
}; 

typedef enum {
	AUTO = -1,
	BMP  = 0,
	JPG,
	GIF,
	TIFF,
	PNG,
	LAST
} tImageType;

/**
 *	Save the specified image.
 *	@param fname the output image filename.
 *	@param I the image to save.
 *	@param imageType one of the supported image types or AUTO to detect from extension.
 *	@throws SaveImageFailedException if save fails for any reason.
 */
void save(const char* fname, const ImageBGRb& I, tImageType imageType = AUTO);
void save(const char* fname, const ImageLb& I, tImageType imageType = AUTO);

void convert(const ImageBGRb& I, ImageBGRf& O);
ImageBGRf convert(const ImageBGRb& I);

void convert(const ImageBGRf& I, ImageBGRb& O);
ImageBGRb convert(const ImageBGRf& I);

void convert(const ImageLb& I, ImageLf& O);
ImageLf convert(const ImageLb& I);

void convert(const ImageLf& I, ImageLb& O);
ImageLb convert(const ImageLf& I);

// GDIPlus conversion
void convert(const Gdiplus::Image* I, ImageBGRb& O);
void convert(const ImageBGRb& I, Gdiplus::Image*& O, bool clone = false);

void convert(const Gdiplus::Image* I, ImageLb& O);
void convert(const ImageLb& I, Gdiplus::Image*& O);

inline Gdiplus::Image* toGdiImage(ImageBGRb& I, Gdiplus::Image*& O) {
	convert(I, O);
	return O;
}

inline Gdiplus::Image* toGdiImage(ImageLb& I, Gdiplus::Image*& O) {
	convert(I, O);
	return O;
}

void splitBlue(const ImageBGRb& I, ImageLb& O);
ImageLb splitBlue(const ImageBGRb& I);
void splitGreen(const ImageBGRb& I, ImageLb& O);
ImageLb splitGreen(const ImageBGRb& I);
void splitRed(const ImageBGRb& I, ImageLb& O);
ImageLb splitRed(const ImageBGRb& I);

void merge(const ImageLb& B, const ImageLb& G, const ImageLb& R, ImageBGRb& O);
ImageBGRb merge(const ImageLb& B, const ImageLb& G, const ImageLb& R);

void grayscale(const ImageBGRb& I, ImageLb& O);
ImageLb grayscale(const ImageBGRb& I);

template<class T>
void gradient(const Image<T>& I, Image<tUV<T> >& O);
template<class T>
Image<tUV<T> > gradient(const Image<T>& I);

template<class T>
void divergents(const Image<tUV<T> >& I, Image<T>& O);
template<class T>
Image<T> divergents(const Image<tUV<T> >& I);

template<class T>
void laplacian(const Image<T>& I, Image<T>& O);
template<class T>
Image<T> laplacian(const Image<T>& I);

#ifndef THROW
#define THROW(x) throw x
#endif /* THROW */

// Colorspace conversions
byte toGrayscale(tBGRb c);
void RGBtoHSV(tBGRb c, double& h, double& s, double& v);
tBGRb HSVtoRGB(double h, double s, double v);

#include "imagelib.inl"

} // namespace imagelib