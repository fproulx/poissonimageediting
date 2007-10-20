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

#include "GraphicsEx.h"
#pragma comment (lib,"Gdiplus.lib")

namespace imagelib {

GraphicsEx::GraphicsEx(IN HDC hdc)
: Graphics(hdc), m_gdiImage(NULL)
{}

GraphicsEx::GraphicsEx(ImageBGRb& I)
: Graphics(toGdiImage(I, m_gdiImage))
{}

GraphicsEx::~GraphicsEx()
{
	delete m_gdiImage;
}

Status GraphicsEx::DrawCross(IN const Pen* pen, IN const PointF& pt, IN REAL extent)
{
	Status s = DrawLine(pen, pt.X - extent, pt.Y, pt.X + extent, pt.Y);
	if (s != Ok) return s;
	return DrawLine(pen, pt.X, pt.Y - extent, pt.X, pt.Y + extent);
}

Status GraphicsEx::DrawCrosses(IN const Pen* pen, IN const PointF* points,
	 						   IN INT count, IN REAL extent)
{
	Status s = Ok;
	for (uint i = 0; i < (uint)count && s == Ok; i++) {
		s = DrawCross(pen, points[i], extent);
	}
	return s;
}

Status GraphicsEx::DrawXCross(IN const Pen* pen, IN PointF pt, IN REAL extent) {
	Status s = DrawLine(pen, pt.X - extent, pt.Y - extent, pt.X + extent, pt.Y + extent);
	if (s != Ok) return s;
	return DrawLine(pen, pt.X + extent, pt.Y - extent, pt.X - extent, pt.Y + extent);
}

Status GraphicsEx::DrawXCrosses(IN const Pen* pen, IN const PointF* points,
								IN INT count, IN REAL extent)
{
	Status s = Ok;
	for (uint i = 0; i < (uint)count && s == Ok; i++) {
		s = DrawXCross(pen, points[i], extent);
	}
	return s;
}

Status GraphicsEx::DrawString(IN const char* str, IN const Font *font, IN const PointF &origin, IN const Brush *brush)
{
	WCHAR* st = new WCHAR[strlen(str)+1];
	int nLen = MultiByteToWideChar(CP_ACP, 0,str, -1, NULL, NULL);
	MultiByteToWideChar(CP_ACP, 0, str, -1, st, nLen);
	Status s = Graphics::DrawString(st, nLen-1, font, origin, brush);
	delete[] st;
	return s;
}

Status GraphicsEx::MeasureString(IN const char* str, IN const Font *font, IN const PointF &origin, OUT RectF *boundingBox) const
{
	WCHAR* st = new WCHAR[strlen(str)+1];
	int nLen = MultiByteToWideChar(CP_ACP, 0,str, -1, NULL, NULL);
	MultiByteToWideChar(CP_ACP, 0, str, -1, st, nLen);
	Status s = Graphics::MeasureString(st, nLen-1, font, origin, boundingBox);
	delete[] st;
	return s;
}

} // namespace imagelib