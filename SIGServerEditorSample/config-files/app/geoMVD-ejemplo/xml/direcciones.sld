<StyledLayerDescriptor version="1.0.0">
	<NamedLayer>
		<Name>Direcciones</Name>
			<UserStyle>
				<Title>Direcciones</Title>
				<FeatureTypeStyle>
					<Rule>
						<Title> </Title>
						<MaxScaleDenominator>2000</MaxScaleDenominator>
						<TextSymbolizer>
							<Label> <PropertyName>numero</PropertyName>	</Label>
							<LabelPlacement>
			                    <PointPlacement>
	          			            <AnchorPoint>
	                        			<AnchorPointX>0.5</AnchorPointX>
				                        <AnchorPointY>0.5</AnchorPointY>
	            			         </AnchorPoint>
			                    </PointPlacement>
            			    </LabelPlacement>
							<Font>
								<CssParameter name="font-family">Lucida Sans</CssParameter>
								<CssParameter name="font-size">9</CssParameter>
							</Font>
							<Fill> <CssParameter name="fill">#000000</CssParameter>	</Fill>
							<Halo>
								<Radius>2</Radius>
				                <Fill> <CssParameter name="fill">#D0D7D9</CssParameter> </Fill>
							</Halo>
							<LabelPlacement>
								<PointPlacement>
									<Displacement>
										<DisplacementX>5</DisplacementX>
										<DisplacementY>0</DisplacementY>
									</Displacement>
								</PointPlacement>
							</LabelPlacement>
						</TextSymbolizer>						
					</Rule>
				</FeatureTypeStyle>
			</UserStyle>
		</NamedLayer>
</StyledLayerDescriptor>