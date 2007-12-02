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

#include <windows.h>
#include <GdiPlus.h>

#include "imagelib.h"

namespace imagelib {
using namespace Gdiplus;

/**
 *  Extension of GDI+ Graphics, supports rendering into an Image<BGRb>.
 */
class GraphicsEx : public Graphics
{
public:
	/**
	 *	Creates a GraphicsEx instance which renders to an HDC.
	 *	@param hdc The HDC to use.
	 *	@see Graphics::Graphics(HDC,...)
	 */
	GraphicsEx(HDC hdc);
	/**
	 *	Creates a GraphicsEx instance which renders to an ImageBGRb image.
	 *	@param I The redner target image.
	 *	@see Graphics::Graphics(HDC,...)
	 */
	GraphicsEx(ImageBGRb& I);

	/**
	 *	Dtor.
	 */
	~GraphicsEx();

	/**
	 *	Draws a single or an array of '+' like crosses.
	 */
	Status DrawCross(const Pen* pen, const PointF& pt, REAL extent);
	Status DrawCrosses(const Pen* pen, const PointF* points, INT count, REAL extent);

	/**
	 *	Draws a single or an array of 'x' like crosses.
	 */
	Status DrawXCross(const Pen* pen, PointF pt, REAL extent);
	Status DrawXCrosses(const Pen* pen, const PointF* points, INT count, REAL extent);

	/**
	 *	DrawString for regular char* strings.
	 *
	 *	@see Graphics::DrawString(WCHAR...)
	 */
	Status DrawString(const char* str, const Font *font, const PointF &origin, const Brush *brush);

	/**
	 *	MeasureString for regular char* strings.
	 *
	 *	@see Graphics::MeasureString(WCHAR...)
	 */
	Status MeasureString(IN const char* str, IN const Font *font, IN const PointF &origin, OUT RectF *boundingBox) const;
private:
	Gdiplus::Image* m_gdiImage;
};

} // namespace imagelib