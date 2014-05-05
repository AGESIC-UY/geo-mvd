<StyledLayerDescriptor version="1.0.0">
	<NamedLayer>
		<Name>Limites</Name>
		<UserStyle>
			<Title>Limites departamento</Title>
			<FeatureTypeStyle>
				<Rule>
				
					<title> </title>
					<PolygonSymbolizer>
						<Fill>
							<CssParameter name="fill"> #E6E6E6 </CssParameter>
						</Fill>
						<Stroke>
							<CssParameter name="stroke">
								#000000
							</CssParameter>
							<CssParameter name="stroke-width">1</CssParameter>
						</Stroke>
					</PolygonSymbolizer>
					<TextSymbolizer>
						<Label>
						<PropertyName>nombre</PropertyName>
						</Label>
						<Font>
							<CssParameter name="font-family">Lucida Sans</CssParameter>
							<CssParameter name="font-size">14</CssParameter>
						</Font>
						<Fill>
							<CssParameter name="fill">#000000</CssParameter>
						</Fill>
   						   <LabelPlacement>
								<PointPlacement>
									<Displacement>
										<DisplacementX>-50</DisplacementX>
										<DisplacementY>50</DisplacementY>
									</Displacement>
								</PointPlacement>
							</LabelPlacement>

					</TextSymbolizer>					
				</Rule>			
			
			</FeatureTypeStyle>
		</UserStyle>
	</NamedLayer>
</StyledLayerDescriptor>